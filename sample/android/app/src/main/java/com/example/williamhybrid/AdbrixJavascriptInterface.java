package com.example.williamhybrid;

import android.content.Context;
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
            JSONObject properties = jsonObject.getJSONObject("sign_up_properties");
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(properties);
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

//            String properties = jsonObject.optString("properties");
//            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(new JSONObject(properties));
//            AdBrixRm.CommonProperties.Purchase purchase = new AdBrixRm.CommonProperties.Purchase();
//            purchase.setAttrModel(attrModel);
            String productModelList = jsonObject.optString("items");
            List<AdBrixRm.CommerceProductModel> commerceProductModelList = getCommerceProductModelListByJsonString(productModelList);
            AdBrixRm.Common.purchase(orderId, commerceProductModelList, orderSales, discount, deliveryCharge, method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addToWishList(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String productModelList = jsonObject.optString("productModelList");
            List<AdBrixRm.CommerceProductModel> commerceProductModelList = getCommerceProductModelListByJsonString(productModelList);
            String properties = jsonObject.optString("properties");
            AdBrixRm.AttrModel attrModel = getAttrModelFromJsonObject(new JSONObject(properties));
            AdBrixRm.Commerce.addToWishList(commerceProductModelList, attrModel);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<AdBrixRm.CommerceProductModel> getCommerceProductModelListByJsonString(String dataJsonString) {
        try {
            JSONArray root = new JSONArray(dataJsonString);

            if (root.length() < 1) {
                Log.e("abxrm", "commerceV2PlugIn error : No purhcase item.");
                return null;
            }

            ArrayList<AdBrixRm.CommerceProductModel> items = new ArrayList<AdBrixRm.CommerceProductModel>();

            for (int i = 0; i < root.length(); i++) {
                try {
                    JSONObject item = root.getJSONObject(i);
                    AdBrixRm.CommerceProductModel pItem = new AdBrixRm.CommerceProductModel();

                    if (item.has("productId")) {
                        Log.i("abxrm", "Productname is " + item.getString("productId"));
                        pItem.setProductID(item.getString("productId"));
                    } else {
                        throw new Exception("No productId attribute.");
                    }
                    if (item.has("productName")) {
                        pItem.setProductName(item.getString("productName"));
                    } else {
                        throw new Exception("No productName attribute.");
                    }
                    if (item.has("price")) {
                        pItem.setPrice(Double.parseDouble(item.getString("price")));
                    } else {
                        throw new Exception("No price attribute.");
                    }
                    if (item.has("discount")) {
                        pItem.setDiscount(Double.parseDouble(item.getString("discount")));
                    } else {
                        throw new Exception("No discount attribute.");
                    }
                    if (item.has("quantity")) {
                        pItem.setQuantity(Integer.parseInt(item.getString("quantity")));
                    } else {
                        throw new Exception("No quantity attribute.");
                    }
                    if (item.has("currency")) {
                        pItem.setCurrency(getCurrencyByCurrencyCode(item.getInt("currency")));
                    } else {
                        throw new Exception("No currency attribute.");
                    }
                    if (item.has("category")) {
                        String[] categories = new String[5];
                        String[] temp = item.getString("category") != null ? item.getString("category").split("\\.") : new String[0];

                        for (int j = 0; j < temp.length; j++) {
                            categories[j] = temp[j];
                        }

                        AdBrixRm.CommerceCategoriesModel categoriesModel = new AdBrixRm.CommerceCategoriesModel();

                        if (categories.length == 1) categoriesModel.setCategory(categories[0]);
                        else if (categories.length == 2)
                            categoriesModel.setCategory(categories[0]).setCategory(categories[1]);
                        else if (categories.length == 3)
                            categoriesModel.setCategory(categories[0]).setCategory(categories[1]).setCategory(categories[2]);
                        else if (categories.length == 4)
                            categoriesModel.setCategory(categories[0]).setCategory(categories[1]).setCategory(categories[2]).setCategory(categories[3]);
                        else if (categories.length == 5)
                            categoriesModel.setCategory(categories[0]).setCategory(categories[1]).setCategory(categories[2]).setCategory(categories[3]).setCategory(categories[4]);
                        pItem.setCategory(categoriesModel);
                    } else {
                        throw new Exception("No category attribute.");
                    }
                    if (item.has("extra_attrs")) {
                        final JSONObject subItem = item.getJSONObject("extra_attrs");
                        JSONObject attrs = new JSONObject();
                        Iterator<?> keys = subItem.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            String value = subItem.getString(key);
                            attrs.put(key, value);
                        }
                        pItem.setAttrModel(getAttrModelFromJsonObject(attrs));
                    } else {
                        throw new Exception("No extra_attrs attribute.");
                    }
                    items.add(pItem);
                } catch (Exception e) {
                    Log.e("abxrm", "purchase error : invalid item = " + dataJsonString);
                }
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AdBrixRm.CommerceCategoriesModel getCommerceCategoriesModelFromList(String categoryString) {
        try {
            String[] temp = categoryString != null ? categoryString.split("\\.") : new String[0];
            AdBrixRm.CommerceCategoriesModel categories = new AdBrixRm.CommerceCategoriesModel();

            if (temp.length == 1) return categories.setCategory(temp[0]);
            else if (temp.length == 2) return categories.setCategory(temp[0]).setCategory(temp[1]);
            else if (temp.length == 3)
                return categories.setCategory(temp[0]).setCategory(temp[1]).setCategory(temp[2]);
            else if (temp.length == 4)
                return categories.setCategory(temp[0]).setCategory(temp[1]).setCategory(temp[2]).setCategory(temp[3]);
            else if (temp.length == 5)
                return categories.setCategory(temp[0]).setCategory(temp[1]).setCategory(temp[2]).setCategory(temp[3]).setCategory(temp[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
