package com.example.chatify.HelperClasses;

import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {

    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"; //this will be same for all projects

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"chatify-4927c\",\n" +
                    "  \"private_key_id\": \"0de4cc01acd949fd647fc7caedca7a707fe84c11\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDX/RIiArSkGgni\\nTuUF0xzKzcY2GJQhc0tkGmidBcu3HAxcnqN967LlX2AJceCihKpvz8PeYwsrL4es\\nZ9Kg5rEnGmxQufD4uxbzD8/NJ/aai73LjDihN/jzyDb7zGkX52hH95TAzoZOHHgg\\n9+E60HL/IOB20I2Xmco5mrXrZJJTIwQCOZMpI7mZ4dGli5JblJhehB96H9yWoQq6\\n998c8YG1JErcHAmZ5aS7+UrR/aPs6NTDuRgceLl8jkLbI5QLpjdYMB2fZ5VwFQ69\\nQqPNs0qUcMTJio9SSLrdme7I4+NmxXnbCKtEuKwZct0s5RyMl9w1XZoYeIxIA5Du\\nQBQBo5SxAgMBAAECggEAOYQezAfEat60OxufSUnKpQbe/ZDWAYJeVS3jJc1hTkWd\\n1Rw5lVKqtQIM5Pb6RszlRhkho5rkYn+ptXOAV3CXRD55EZYyYUoQ9nEDeTJnLQiB\\nZi7U9bmeBe0PF0Z7xe3NizyW1DEq+qoCI2VfCPZxaRcsLveifPhrLaPRRfnmLVTs\\nG24NmqQNyj7nIAkTAGynqzimJpIZnTNYAXAD1gG3eGruKwBqwWIGRMdxRytLrkpf\\n4tX4+jdWUu2I5YtPKazEg93CuUi01GXTDmlCOU54EHzci2LFagnx4FMbp7/e2su5\\n6gfeoEzfReemRsOqET1xi6jZRSmyMq6SkV9poRiL8QKBgQD+5ykiAfpo5uP787FW\\nddDR3liI1dHTypIbekb8mxCqLMjdGvZ0UtsubV52Dshn6ZTUC7L2FYtp5m7ucEpi\\nSv4bMbiA/AWZ8FAuMoRHNZtovArWaObZrB0OZ33bkFMjh8UeAcQwUli/1f6q/bk3\\nIagf58BmyzKqDz1bIlYglRqMewKBgQDY6wlEucQ2JO281P76kexCiromLEBrQ67d\\nlHqXbpwrlTsAiBOaTBNTw9AT+eKE7EwbwBgIFv/YJiwQmjJAwkz0iQIbJ/A9I/ZK\\nJ2beHINa6FggmZCC0xEJT5qyTmZKAxuD1uTyhj5d73Jf21rS+v3G2TnZf865Qsvi\\nIGCMUvrJwwKBgQDvrf4gSwnhT5F5nBfYu2lBnTdUWvE0mxotGhnbdhcCXJ/2P18T\\n28Dac95JVfcctibKB8Ib4AA58IHIU++tYuRyP5tENTUzHSLXfYqhxoc9wOrT1smS\\nTWL441kVdiymkQhJAdIuuY9Xumj+8RoR++SxM2HXhiM6gp/nrG6drnXLNwKBgQC2\\nyyGtW4ogQnBgz3DrQKrCzFrfriy+93NVYDkKgXhzNbdZ1FIIlZE35mvjWZFsiB/+\\nxpq5vn/s6wX5G6e7cMEe1JIExlwpebOje56xD4AVzHPvTNk9lZXmrcFj/rMuLX4z\\nVJTsVvoZeJ1+b8ASyNWwiv+37H8NGV/sd3JE7exOtwKBgCWRhGIaBQhZNiBgdUIL\\n9BXQUsSvcQ3iK/qGsn8Pp9naZH/rsCV2f9hZYBPzJMq2EeabbsGEvHUnhsQrRRni\\nM7hdGDdtG+15jkyUjgNyQzc6OUBR+MWAz1Q+WPMKbNNhknxm++S4XvP6ZEbdxfb4\\nRveEOyZ1NOXkNcOxlVQCTyHF\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-izw8s@chatify-4927c.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"112556843478433888501\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-izw8s%40chatify-4927c.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Lists.newArrayList(firebaseMessagingScope));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return null;
        }
    }
}
