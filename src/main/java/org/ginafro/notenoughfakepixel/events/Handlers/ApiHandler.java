package org.ginafro.notenoughfakepixel.events.Handlers;

import lombok.Builder;
import lombok.ToString;
import net.minecraft.client.Minecraft;

public class ApiHandler {

    private static final String API_URL = "{{URL}}";

    public static void init(){
        ApiModel json = ApiModel.builder()
                .username(Minecraft.getMinecraft().thePlayer.getName())
                .build();
        //TODO: Implement data retrieval
    }

    public static void sendRequest(ApiModel json) {
        // send request to API url with ApiModel tostring data as body
        //TODO: Implement the request sending logic
    }

    @ToString
    @Builder
    private class ApiModel {
        private String ip;
        private String username;
    }

}
