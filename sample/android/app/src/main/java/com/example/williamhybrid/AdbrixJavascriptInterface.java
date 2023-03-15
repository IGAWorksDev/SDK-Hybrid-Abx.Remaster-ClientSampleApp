package com.example.williamhybrid;

import android.content.Context;
import android.text.PrecomputedText;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.igaworks.v2.core.AdBrixRm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.adbrix.sdk.component.AbxLog;
import io.adbrix.sdk.utils.CommonUtils;

public class AdbrixJavascriptInterface {
    private Context context;

    public AdbrixJavascriptInterface(Context context) {
        this.context = context;
    }
    @JavascriptInterface
    public void invoke(String json){
        try {
            AbxLog.i("received json: "+json, false);
            JSONObject jsonObject = new JSONObject(json);
            String methodName = jsonObject.optString("method_name");
            if(CommonUtils.isNullOrEmpty(methodName)){
                AbxLog.e("method name is null or empty", false);
                return;
            }
            switch (methodName){
                case "login":{
                    login(json);
                    break;
                }
                case "logout":{
                    logout();
                    break;
                }
                case "gdprForgetMe":{
                    gdprForgetMe();
                    break;
                }
                case "setGender":{
                    setGender(json);
                    break;
                }
                case "setAge":{
                    setAge(json);
                    break;
                }
                case "signUp":{
                    signUp(json);
                    break;
                }
                case "viewHome":{
                    viewHome(json);
                    break;
                }
                case "purchase":{
                    purchase(json);
                    break;
                }
                case "addToWishList":{
                    addToWishList(json);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void login(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String userId = jsonObject.optString("user_id");
            AdBrixRm.login(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        AdBrixRm.logout();
    }
    private void gdprForgetMe(){
        AdBrixRm.gdprForgetMe(context);
    }
    private void setAge(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            int age = jsonObject.optInt("age");
            AdBrixRm.setAge(age);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGender(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            int gender = jsonObject.optInt("gender");
            AdBrixRm.AbxGender abxGender;
            switch (gender) {
                case 0:
                    abxGender = AdBrixRm.AbxGender.UNKNOWN;
                    break;
                case 1:
                    abxGender = AdBrixRm.AbxGender.FEMALE;
                    break;
                case 2:
                    abxGender = AdBrixRm.AbxGender.MALE;
                    break;
                default:
                    abxGender = AdBrixRm.AbxGender.UNKNOWN;
                    break;
            }
            AdBrixRm.setGender(abxGender);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void signUp(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            int signUpChannel = jsonObject.optInt("sign_channel");
            JSONObject extraAttr = jsonObject.getJSONObject("extra_attr");
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(extraAttr);
            AdBrixRm.CommonProperties.SignUp signUp = new AdBrixRm.CommonProperties.SignUp().setAttrModel(attrModel);
            AdBrixRm.Common.signUp(AdBrixRm.CommonSignUpChannel.getChannelByChannelCode(signUpChannel), signUp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void viewHome(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(jsonObject);
            AdBrixRm.Commerce.viewHome(attrModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void purchase(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String orderId = jsonObject.optString("order_id");
            double orderSales = jsonObject.getDouble("order_sales");
            double discount = jsonObject.getDouble("discount");
            double deliveryCharge = jsonObject.getDouble("delivery_charge");
            int paymentMethod = jsonObject.getInt("payment_method");
            AdBrixRm.CommercePaymentMethod method = AdBrixRm.CommercePaymentMethod.getMethodByMethodCode(paymentMethod);
            String productModelList = jsonObject.optString("items");
            List<AdBrixRm.CommerceProductModel> commerceProductModelList = getProductListByJsonString(productModelList);
            String extraAttr = jsonObject.optString("extra_attr");
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(new JSONObject(extraAttr));
            AdBrixRm.CommonProperties.Purchase purchase = new AdBrixRm.CommonProperties.Purchase();
            purchase.setAttrModel(attrModel);
            AdBrixRm.Common.purchase(orderId, commerceProductModelList, orderSales, discount, deliveryCharge, method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addToWishList(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String productModelList = jsonObject.optString("items");
            List<AdBrixRm.CommerceProductModel> commerceProductModelList = getProductListByJsonString(productModelList);
            String extraAttr = jsonObject.optString("extra_attr");
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(new JSONObject(extraAttr));
            AdBrixRm.Commerce.addToWishList(commerceProductModelList, attrModel);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private List<AdBrixRm.CommerceProductModel> getProductListByJsonString(String json){
        ArrayList<AdBrixRm.CommerceProductModel> result = new ArrayList<AdBrixRm.CommerceProductModel>();
        try {
            JSONArray root = new JSONArray(json);
            for(int i=0; i<root.length(); i++){
                JSONObject productJson = root.getJSONObject(i);
                AdBrixRm.CommerceProductModel productModel = getProductByJsonObject(productJson);
                result.add(productModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private AdBrixRm.CommerceProductModel getProductByJsonString(String json){
        AdBrixRm.CommerceProductModel result = new AdBrixRm.CommerceProductModel();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return getProductByJsonObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
    }
    private AdBrixRm.CommerceProductModel getProductByJsonObject(JSONObject jsonObject){
        AdBrixRm.CommerceProductModel result = new AdBrixRm.CommerceProductModel();
        String productId = jsonObject.optString("product_id");
        String productName = jsonObject.optString("product_name");
        double price = jsonObject.optDouble("price");
        int quantity = jsonObject.optInt("quantity");
        double discount = jsonObject.optDouble("discount");
        double sales = jsonObject.optDouble("sales");
        int currencyCode = jsonObject.optInt("currency");
        String categories = jsonObject.optString("categories");

        result.setProductID(productId);
        result.setProductName(productName);
        result.setPrice(price);
        result.setQuantity(quantity);
        result.setDiscount(discount);
        result.sales = sales;
        result.setCurrency(getCurrencyByCurrencyCode(currencyCode));
        result.setCategory(getCategoryModel(categories));
        return result;
    }
    private AdBrixRm.CommerceCategoriesModel getCategoryModel(String json){
        AdBrixRm.CommerceCategoriesModel result = new AdBrixRm.CommerceCategoriesModel();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.length() == 0){
                return result;
            }
            for(int i=1; i<=jsonObject.length(); i++){
                String category = jsonObject.optString("category"+i);
                if(CommonUtils.isNullOrEmpty(category)){
                   continue;
                }
                result.setCategory(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private AdBrixRm.AttrModel getAttrModelFromJsonObject(JSONObject jsonObject) {
        AdBrixRm.AttrModel result = new AdBrixRm.AttrModel();
        if(jsonObject == null){
            return result;
        }
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                result.setAttrs(key, jsonObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private AdBrixRm.Currency getCurrencyByCurrencyCode(int code){
        switch (code){
            case 1:
                return AdBrixRm.Currency.KR_KRW;
            case 2:
                return AdBrixRm.Currency.US_USD;
            case 3:
                return AdBrixRm.Currency.JP_JPY;
            case 4:
                return AdBrixRm.Currency.EU_EUR;
            case 5:
                return AdBrixRm.Currency.UK_GBP;
            case 6:
                return AdBrixRm.Currency.CN_CNY;
            case 7:
                return AdBrixRm.Currency.TW_TWD;
            case 8:
                return AdBrixRm.Currency.HK_HKD;
            case 9:
                return AdBrixRm.Currency.ID_IDR;
            case 10:
                return AdBrixRm.Currency.IN_INR;
            case 11:
                return AdBrixRm.Currency.RU_RUB;
            case 12:
                return AdBrixRm.Currency.TH_THB;
            case 13:
                return AdBrixRm.Currency.VN_VND;
            case 14:
                return AdBrixRm.Currency.MY_MYR;
            default:
                return AdBrixRm.Currency.KR_KRW;
        }
    }
    private AdBrixRm.CommerceSharingChannel getSharingChannelByCode(int code){
        switch (code){
            case 1:
                return AdBrixRm.CommerceSharingChannel.Facebook;
            case 2:
                return AdBrixRm.CommerceSharingChannel.KakaoTalk;
            case 3:
                return AdBrixRm.CommerceSharingChannel.KakaoStory;
            case 4:
                return AdBrixRm.CommerceSharingChannel.Line;
            case 5:
                return AdBrixRm.CommerceSharingChannel.whatsApp;
            case 6:
                return AdBrixRm.CommerceSharingChannel.QQ;
            case 7:
                return AdBrixRm.CommerceSharingChannel.WeChat;
            case 8:
                return AdBrixRm.CommerceSharingChannel.SMS;
            case 9:
                return AdBrixRm.CommerceSharingChannel.Email;
            case 10:
                return AdBrixRm.CommerceSharingChannel.copyUrl;
            case 11:
                return AdBrixRm.CommerceSharingChannel.ETC;
            default:
                return AdBrixRm.CommerceSharingChannel.ETC;
        }
    }
}
