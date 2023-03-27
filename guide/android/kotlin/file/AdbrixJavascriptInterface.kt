package com.example.williamhybrid

import android.content.Context
import android.webkit.JavascriptInterface
import com.igaworks.v2.core.AdBrixRm
import com.igaworks.v2.core.AdBrixRm.*
import com.igaworks.v2.core.AdBrixRm.CommonProperties.Purchase
import com.igaworks.v2.core.AdBrixRm.CommonProperties.SignUp
import io.adbrix.sdk.component.AbxLog
import io.adbrix.sdk.utils.CommonUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AdbrixJavascriptInterface(private val context: Context) {

    @JavascriptInterface
    fun invoke(json: String) {
        try {
            AbxLog.i("received json: $json", false)
            val jsonObject = JSONObject(json)
            val methodName = jsonObject.optString("method_name")
            if (CommonUtils.isNullOrEmpty(methodName)) {
                AbxLog.e("method name is null or empty", false)
                return
            }
            when (methodName) {
                "login" -> {
                    login(json)
                }
                "logout" -> {
                    logout()
                }
                "gdprForgetMe" -> {
                    gdprForgetMe()
                }
                "setGender" -> {
                    setGender(json)
                }
                "setAge" -> {
                    setAge(json)
                }
                "signUp" -> {
                    signUp(json)
                }
                "viewHome" -> {
                    viewHome(json)
                }
                "purchase" -> {
                    purchase(json)
                }
                "addToWishList" -> {
                    addToWishList(json)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun login(json: String) {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(json)
            val userId = jsonObject.optString("user_id")
            AdBrixRm.login(userId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun logout() {
        AdBrixRm.logout()
    }

    private fun gdprForgetMe() {
        gdprForgetMe(context)
    }

    private fun setAge(json: String) {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(json)
            val age = jsonObject.optInt("age")
            setAge(age)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setGender(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val gender = jsonObject.optInt("gender")
            val abxGender: AbxGender
            abxGender = when (gender) {
                0 -> AbxGender.UNKNOWN
                1 -> AbxGender.FEMALE
                2 -> AbxGender.MALE
                else -> AbxGender.UNKNOWN
            }
            setGender(abxGender)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun signUp(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val signUpChannel = jsonObject.optInt("sign_channel")
            val extraAttr = jsonObject.getJSONObject("extra_attr")
            val attrModel = getAttrModelFromJsonObject(extraAttr)
            val signUp = SignUp().setAttrModel(attrModel)
            Common.signUp(CommonSignUpChannel.getChannelByChannelCode(signUpChannel), signUp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun viewHome(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val attrModel = getAttrModelFromJsonObject(jsonObject)
            Commerce.viewHome(attrModel)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun purchase(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val orderId = jsonObject.optString("order_id")
            val orderSales = jsonObject.getDouble("order_sales")
            val discount = jsonObject.getDouble("discount")
            val deliveryCharge = jsonObject.getDouble("delivery_charge")
            val paymentMethod = jsonObject.getInt("payment_method")
            val method = CommercePaymentMethod.getMethodByMethodCode(paymentMethod)
            val productModelList = jsonObject.optString("items")
            val commerceProductModelList = getProductListByJsonString(productModelList)
            val extraAttr = jsonObject.optString("extra_attr")
            val attrModel = getAttrModelFromJsonObject(JSONObject(extraAttr))
            val purchase = Purchase()
            purchase.setAttrModel(attrModel)
            Common.purchase(
                orderId,
                commerceProductModelList,
                orderSales,
                discount,
                deliveryCharge,
                method
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addToWishList(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val productModelList = jsonObject.optString("items")
            val commerceProductModelList = getProductListByJsonString(productModelList)
            val extraAttr = jsonObject.optString("extra_attr")
            val attrModel = getAttrModelFromJsonObject(JSONObject(extraAttr))
            Commerce.addToWishList(commerceProductModelList, attrModel)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getProductListByJsonString(json: String): List<CommerceProductModel> {
        val result = ArrayList<CommerceProductModel>()
        try {
            val root = JSONArray(json)
            for (i in 0 until root.length()) {
                val productJson = root.getJSONObject(i)
                val productModel = getProductByJsonObject(productJson)
                result.add(productModel)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getProductByJsonString(json: String): CommerceProductModel? {
        val result = CommerceProductModel()
        return try {
            val jsonObject = JSONObject(json)
            getProductByJsonObject(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
            result
        }
    }

    private fun getProductByJsonObject(jsonObject: JSONObject): CommerceProductModel {
        val result = CommerceProductModel()
        val productId = jsonObject.optString("product_id")
        val productName = jsonObject.optString("product_name")
        val price = jsonObject.optDouble("price")
        val quantity = jsonObject.optInt("quantity")
        val discount = jsonObject.optDouble("discount")
        val sales = jsonObject.optDouble("sales")
        val currencyCode = jsonObject.optInt("currency")
        val categories = jsonObject.optString("categories")
        result.setProductID(productId)
        result.setProductName(productName)
        result.setPrice(price)
        result.setQuantity(quantity)
        result.setDiscount(discount)
        result.sales = sales
        result.setCurrency(getCurrencyByCurrencyCode(currencyCode))
        result.setCategory(getCategoryModel(categories))
        return result
    }

    private fun getCategoryModel(json: String): CommerceCategoriesModel? {
        val result = CommerceCategoriesModel()
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.length() == 0) {
                return result
            }
            for (i in 1..jsonObject.length()) {
                val category = jsonObject.optString("category$i")
                if (CommonUtils.isNullOrEmpty(category)) {
                    continue
                }
                result.setCategory(category)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getAttrModelFromJsonObject(jsonObject: JSONObject?): AttrModel {
        val result = AttrModel()
        if (jsonObject == null) {
            return result
        }
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            try {
                result.setAttrs(key, jsonObject[key])
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun getCurrencyByCurrencyCode(code: Int): Currency? {
        return when (code) {
            1 -> Currency.KR_KRW
            2 -> Currency.US_USD
            3 -> Currency.JP_JPY
            4 -> Currency.EU_EUR
            5 -> Currency.UK_GBP
            6 -> Currency.CN_CNY
            7 -> Currency.TW_TWD
            8 -> Currency.HK_HKD
            9 -> Currency.ID_IDR
            10 -> Currency.IN_INR
            11 -> Currency.RU_RUB
            12 -> Currency.TH_THB
            13 -> Currency.VN_VND
            14 -> Currency.MY_MYR
            else -> Currency.KR_KRW
        }
    }

    private fun getSharingChannelByCode(code: Int): CommerceSharingChannel? {
        return when (code) {
            1 -> CommerceSharingChannel.Facebook
            2 -> CommerceSharingChannel.KakaoTalk
            3 -> CommerceSharingChannel.KakaoStory
            4 -> CommerceSharingChannel.Line
            5 -> CommerceSharingChannel.whatsApp
            6 -> CommerceSharingChannel.QQ
            7 -> CommerceSharingChannel.WeChat
            8 -> CommerceSharingChannel.SMS
            9 -> CommerceSharingChannel.Email
            10 -> CommerceSharingChannel.copyUrl
            11 -> CommerceSharingChannel.ETC
            else -> CommerceSharingChannel.ETC
        }
    }
}