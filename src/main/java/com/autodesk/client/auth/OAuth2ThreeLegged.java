/*
 * Forge SDK
 * The Forge Platform contains an expanding collection of web service components that can be used with Autodesk cloud-based products or your own technologies. Take advantage of Autodesk’s expertise in design and engineering.
 *
 * OpenAPI spec version: 0.1.0
 * Contact: forge.help@autodesk.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.autodesk.client.auth;

import com.autodesk.client.ApiException;
import com.autodesk.client.Pair;
import com.autodesk.client.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class OAuth2ThreeLegged implements Authentication {

    private String name;
    private String type;
    private OAuthFlow flow;
    private String tokenUrl;
    private String authorizationUrl;
    private String refreshTokenUrl;
    private List<String> scopes;
    private List<String> selectedScopes;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Boolean autoRefresh;
    private ThreeLeggedCredentials credentials;

    // makes a POST request to url with form parameters and returns body as a string
    private String post(String url, Map<String, String> formParameters, Map<String, String> headers)
            throws ClientProtocolException, IOException, ApiException {
        HttpPost request = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        for (String key : headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }

        for (String key : formParameters.keySet()) {
            nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
        }

        request.setEntity(new UrlEncodedFormEntity(nvps));

        return execute(request);
    }

    // makes request and checks response code for 200
    private String execute(HttpRequestBase request) throws ClientProtocolException, IOException, ApiException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            throw new ApiException(status, body);
        }

        return body;
    }

    private String getScopes() {
        String scopeStr = "";
        if (!selectedScopes.isEmpty()) {
            int index = 0;
            for (String key : selectedScopes) {
                index++;
                if (scopes.contains(key)) {
                    scopeStr += key;
                    if (index < selectedScopes.size())
                        scopeStr += "%20";

                }
            }
        }
        return scopeStr;
    }

    // validates that the selected scopes are not empty and also included in the
    // list of all scopes.
    private Boolean validateScopes(List<String> selectedScopes) throws Exception {
        if (this.scopes.size() > 0) {
            if (selectedScopes != null && selectedScopes.size() > 0) {
                for (String key : selectedScopes) {
                    if (!this.scopes.contains(key)) {
                        throw new Exception(key + " scope is not allowed");
                    }
                }
            } else {
                // throw if scope is null or undefined
                throw new Exception("Scope is missing or empty, you must provide a valid scope");
            }
        } else {
            throw new Exception("Authentication does not allow any scopes");
        }
        return true;
    }

    /**
     * OAuth2ThreeLegged Constructor
     * 
     * @param clientId       - the client id of the application
     * @param clientSecret   - the client secret of the application
     * @param redirectUri    - the redirect URI of the application
     * @param selectedScopes - the scope permissions used to generated access token
     * @param autoRefresh    - set autoRefresh to 'true' to automatically refresh
     *                       the access token when it expires
     * @throws Exception
     */
    public OAuth2ThreeLegged(String clientId, String clientSecret, String redirectUri, List<String> selectedScopes,
            Boolean autoRefresh) throws Exception {

        this.flow = OAuthFlow.accessCode;
        this.scopes = new ArrayList<String>();
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.selectedScopes = selectedScopes;
        this.autoRefresh = autoRefresh;

        this.name = "oauth2_access_code";
        this.type = "oauth2";
        this.tokenUrl = Configuration.getDefaultApiClient().getBasePath() + "/authentication/v1/gettoken";
        this.authorizationUrl = Configuration.getDefaultApiClient().getBasePath() + "/authentication/v1/authorize";
        this.refreshTokenUrl = Configuration.getDefaultApiClient().getBasePath() + "/authentication/v1/refreshtoken";
        this.scopes.add("data:read");
        this.scopes.add("data:write");
        this.scopes.add("data:create");
        this.scopes.add("data:search");
        this.scopes.add("bucket:create");
        this.scopes.add("bucket:read");
        this.scopes.add("bucket:update");
        this.scopes.add("bucket:delete");
        this.scopes.add("code:all");
        this.scopes.add("account:read");
        this.scopes.add("account:write");
        this.scopes.add("user-profile:read");
        this.scopes.add("viewables:read");

        validateScopes(selectedScopes);
    }

    @Override
    @Deprecated
    public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
    }

    public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams,
            ThreeLeggedCredentials credentials) {
        if (credentials != null && credentials.getAccessToken() != null) {
            headerParams.put("Authorization", "Bearer " + credentials.getAccessToken());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setSelectedScopes(List<String> selectedScopes) throws Exception {
        if (validateScopes(selectedScopes)) {
            this.selectedScopes = selectedScopes;
        }
    }

    public Boolean isAutoRefresh() {
        return this.autoRefresh;
    }

    /**
     * Get the authentication url for a 3-legged flow. Redirect the user to this url
     * for authorizing your application.
     * 
     * @return
     */
    public String getAuthenticationUrl() throws Exception {

        if (flow == OAuthFlow.accessCode) {
            // build the URL
            StringBuilder oauthUrl = new StringBuilder().append(this.authorizationUrl).append("?client_id=")
                    .append(this.clientId).append("&response_type=code").append("&redirect_uri=")
                    .append(this.redirectUri);

            String scopeStr = getScopes();
            if (!scopeStr.isEmpty()) {
                oauthUrl.append("&scope=").append(scopeStr);
            }

            return oauthUrl.toString();
        } else {
            throw new Exception("getAuthToken requires accessCode flow type");
        }
    }

    /**
     * Get the access token for a 3-legged flow
     * 
     * @return
     */
    public ThreeLeggedCredentials getAccessToken(String code) throws Exception {

        if (flow == OAuthFlow.accessCode) {

            Map<String, String> formParams = new HashMap<>();
            formParams.put("client_id", this.clientId);
            formParams.put("client_secret", this.clientSecret);
            formParams.put("code", code);
            formParams.put("grant_type", "authorization_code");
            formParams.put("redirect_uri", this.redirectUri);

            Map<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/x-www-form-urlencoded");

            ThreeLeggedCredentials response = null;
            try {
                String responseBody = post(this.tokenUrl, formParams, headers);

                JSONObject jsonObject = null;

                // get the access token from json
                try {
                    jsonObject = (JSONObject) new JSONParser().parse(responseBody);

                    String access_token = (String) jsonObject.get("access_token");
                    String refresh_token = (String) jsonObject.get("refresh_token");
                    // calculate "expires at"
                    long expires_in = (long) jsonObject.get("expires_in");
                    DateTime later = DateTime.now().plusSeconds((int) expires_in);
                    Long expiresAt = later.toDate().getTime();

                    response = new ThreeLeggedCredentials(access_token, expiresAt, refresh_token);

                } catch (ParseException e) {
                    throw new RuntimeException("Unable to parse json " + responseBody);
                }

            } catch (IOException e) {
                System.err.println("Exception when trying to get access token");
                e.printStackTrace();
            }
            return response;
        } else {
            throw new Exception("getAuthToken requires accessCode flow type");
        }
    }

    /**
     * Refresh the access token for a 3-legged flow
     * 
     * @return
     */
    public ThreeLeggedCredentials refreshAccessToken(String refreshToken) {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", this.clientId);
        formParams.put("client_secret", this.clientSecret);
        formParams.put("grant_type", "refresh_token");
        formParams.put("refresh_token", refreshToken);

        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");

        ThreeLeggedCredentials response = null;
        try {
            String responseBody = post(this.refreshTokenUrl, formParams, headers);

            JSONObject jsonObject = null;

            // get the access token from json
            try {
                jsonObject = (JSONObject) new JSONParser().parse(responseBody);

                String access_token = (String) jsonObject.get("access_token");
                String refresh_token = (String) jsonObject.get("refresh_token");
                // calculate "expires at"
                long expires_in = (long) jsonObject.get("expires_in");
                DateTime later = DateTime.now().plusSeconds((int) expires_in);
                Long expiresAt = later.toDate().getTime();

                // should we delete the last this.credentials?
                this.credentials = new ThreeLeggedCredentials(access_token, expiresAt, refresh_token);
                response = this.credentials;

                // refresh access token 3 minutes (3*60 seconds) in advance.
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // get token again
                        try {
                            refreshAccessToken(refresh_token);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, (expires_in - 3 * 60) * 1000);

            } catch (ParseException e) {
                throw new RuntimeException("Unable to parse json " + responseBody);
            }

        } catch (IOException e) {
            System.err.println("Exception when trying to refresh token");
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when trying to refresh token");
            e.printStackTrace();
        }
        return response;
    }

    public Boolean isAuthorized(ThreeLeggedCredentials credentials) {
        return (credentials != null)
                && (credentials.getExpiresAt() != null && (credentials.getExpiresAt() > (new Date().getTime())));
    }
}
