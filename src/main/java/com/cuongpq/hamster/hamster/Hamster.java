package com.cuongpq.hamster.hamster;

import com.cuongpq.hamster.utils.CommonUtil;
import com.cuongpq.hamster.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cuongpq.hamster.utils.FileUtil.readTokens;

@Service
@Slf4j
public class Hamster {

    private static final String KEY = "hamster.token";

    record Promo(String promoId, Integer keysPerDay) {
    }

    record State(String promoId, Integer receiveKeysToday) {
    }

    record Promos(List<Promo> promos, List<State> states) {
    }

    private Map<String, String> getHeaders(String token) {
        return Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token,
                "Origin", "https://hamsterkombatgame.io",
                "Referer", "https://hamsterkombatgame.io"
        );
    }

    private List<Promo> getPromos(String token) {
        try {
            String url = "https://api.hamsterkombatgame.io/interlude/get-promos";
            String res = HttpClientUtil.sendPost(url, new JSONObject(), getHeaders(token));
            Promos promos = CommonUtil.fromJson(res, Promos.class);
            if (promos == null || CollectionUtils.isEmpty(promos.promos) || CollectionUtils.isEmpty(promos.states)) {
                return List.of();
            }
            Map<String, State> availablePromos = promos.states.stream().filter(e -> e.receiveKeysToday < 4)
                    .collect(Collectors.toMap(State::promoId, Function.identity()));
            List<Promo> newAvailablePromos = new ArrayList<>();
            promos.promos.forEach(e -> {
                if (availablePromos.containsKey(e.promoId)) {
                    newAvailablePromos.add(new Promo(e.promoId, 4 - availablePromos.get(e.promoId).receiveKeysToday));
                }
            });
            if (CollectionUtils.isEmpty(newAvailablePromos)) {
                log.info("Already received all available promo codes. Wait till the next day.");
            }
            return newAvailablePromos;
        } catch (Exception e) {
            log.error("Fail to get promos.");
            log.error(e.getMessage());
            return List.of();
        }
    }

    record LoginClient(String clientToken) {
    }

    private String loginClient(String promoId) {
        try {
            String url = "https://api.gamepromo.io/promo/login-client";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appToken", promoId);
            jsonObject.put("clientId", UUID.randomUUID());
            jsonObject.put("clientOrigin", "ios");
            String res = HttpClientUtil.sendPost(url, jsonObject, Map.of());
            LoginClient loginClient = CommonUtil.fromJson(res, LoginClient.class);
            return loginClient == null ? null : loginClient.clientToken();
        } catch (Exception e) {
            log.error("Fail to get promos.");
            log.error(e.getMessage());
            return "";
        }
    }

    record PromoCode(String promoCode) {
    }

    private String createCode(String clientToken, String promoId) {
        try {
            if (!StringUtils.hasText(clientToken)) {
                log.error("Client code not exists.");
                return "";
            }
            boolean hasCode = false;
            int count = 0;
            while (!hasCode) {
                hasCode = registerEvent(promoId, clientToken);
                count++;
                if (count % 100 == 0) {
                    log.info(String.format("Register times: %d", count));
                }
            }
            log.info(String.format("Register for '%s' takes %d times.", promoId, count));
            String url = "https://api.gamepromo.io/promo/create-code";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("promoId", promoId);
            String res = HttpClientUtil.sendPost(url, jsonObject, Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken));
            PromoCode promoCode = CommonUtil.fromJson(res, PromoCode.class);
            return promoCode == null ? null : promoCode.promoCode();
        } catch (Exception e) {
            log.error("Fail to create code.");
            log.error(e.getMessage());
            return "";
        }
    }

    record InterludeUser(String id, Double balanceDiamonds) {
    }

    record ApplyPromoInfo(InterludeUser interludeUser) {
    }

    private void applyPromo(String token, String promoCode) {
        try {
            if (!StringUtils.hasText(promoCode)) {
                log.error("Promo code does not exists.");
                return;
            }
            String url = "https://api.hamsterkombatgame.io/interlude/apply-promo";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("promoCode", promoCode);
            String res = HttpClientUtil.sendPost(url, jsonObject, getHeaders(token));
            ApplyPromoInfo applyPromoInfo = CommonUtil.fromJson(res, ApplyPromoInfo.class);
            if (applyPromoInfo == null || applyPromoInfo.interludeUser() == null) {
                log.error("Fail to apply promo. Please check");
            } else {
                log.info(String.format("Apply promo successfully. Balance diamonds: %s", applyPromoInfo.interludeUser().balanceDiamonds()));
            }
        } catch (Exception e) {
            log.error("Fail to apply promo.");
            log.error(e.getMessage());
        }
    }

    record RegisterEvent(boolean hasCode) {
    }

    private boolean registerEvent(String promoId, String clientToken) {
        try {
            String url = "https://api.gamepromo.io/promo/register-event";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eventId", UUID.randomUUID());
            jsonObject.put("eventOrigin", "undefined");
            jsonObject.put("promoId", promoId);
            String res = HttpClientUtil.sendPost(url, jsonObject, Map.of("Authorization", "Bearer " + clientToken));
            RegisterEvent registerEvent = CommonUtil.fromJson(res, RegisterEvent.class);
            return registerEvent != null && registerEvent.hasCode();
        } catch (Exception e) {
            log.error("Fail to register event.");
            log.error(e.getMessage());
            return false;
        }
    }

    @Scheduled(cron = "0 10 7 ? * *")
    @EventListener(ApplicationReadyEvent.class)
    public void action() {
        log.info("================ Start Hamster ================");
        readTokens(KEY).forEach(e -> new Thread(() -> {
            List<Promo> promos = getPromos(e);
            promos.forEach(o -> {
                for (int i = 0; i < o.keysPerDay(); i++) {
                    String clientToken = loginClient(o.promoId());
                    if (!StringUtils.hasText(clientToken)) {
                        continue;
                    }
                    String code = createCode(clientToken, o.promoId());
                    applyPromo(e, code);
                }
            });
            log.info("Congratulation. You get all available codes.");
        }).start());
        log.info("================ End Hamster ================");
    }
}
