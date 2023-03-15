//
//  AdbrixJavascriptInterface.swift
//  HybridTest
//
//  Created by Jimmy.강세훈 on 2023/03/14.
//

import Foundation
import AdBrixRmKit

enum AdbrixJavascriptInterface: String {
    case login
    case logout
    case signUp
    case purchase
    case viewHome
    
    func invoke(json: [String: Any]) {
        let adbrix = AdBrixRM.sharedInstance()
        
        switch self {
            
        case .login:
            guard let userId = json["user_id"] as? String else {return}
            adbrix.login(userId: userId)
            
        case .logout:
            adbrix.logout()
            
        case .signUp:
            guard let channelValue = json["sign_channel"] as? Int,
                  let channel = AdBrixRM.AdBrixRmSignUpChannel(rawValue: channelValue),
                  let properties = json["sign_up_properties"] as? [String: Any],
                  let userId = properties["user_id"] as? String,
                  let gender = properties["gender"] as? Int,
                  let age = properties["age"] as? Int else {return}
            
            let attr = AdBrixRmAttrModel()
            attr.setAttrDataString("user_id", userId)
            attr.setAttrDataInt("gender", gender)
            attr.setAttrDataInt("age", age)
            
            adbrix.commonSignUpWithAttr(channel: channel, commonAttr: attr)
            
        case .purchase:
            guard let orderId = json["order_id"] as? String,
                  let orderSales = json["order_sales"] as? Double,
                  let discount = json["discount"] as? Double,
                  let deliveryCharge = json["delivery_charge"] as? Double,
                  let paymentMethodValue = json["payment_method"] as? Int,
                  let quantity = json["quantity"] as? Int,
                  let items = json["items"] as? NSArray else {return}
            
            let productModel = AdBrixRmCommerceProductModel()
            for (value) in items {
                guard let map = value as? [String: Any] else {return}
                print(map)
                guard let productId = map["product_id"] as? String,
                      let productName = map["product_name"] as? String,
                      let price = map["price"] as? Double,
                      let quantity = map["quantity"] as? Int,
                      let discount = map["discount"] as? Double,
                      let currencyNum = map["currency"] as? Int,
                      let categoryString = map["categories"] as? [String:String]
                else {return}
                
                var categoryArray = [String](repeating: "", count: 5)
                var idx = 0
                for (_, value) in categoryString {
                    categoryArray[idx] = value
                    idx = idx + 1
                }
                
                let currencyString = adbrix.getCurrencyString(currencyNum)
                let category = adbrix.createCommerceProductCategoryData(category: categoryArray[0], category2: categoryArray[1], category3: categoryArray[2], category4: categoryArray[3], category5: categoryArray[4])
            
                productModel.setModel(productId: productId, productName: productName, price: price, quantity: quantity, discount: discount, currencyString: currencyString, categories: category, productAttrsMap: nil)
            }
            
            adbrix.commonPurchase(orderId: orderId, productInfo: [productModel], discount: discount, deliveryCharge: deliveryCharge, paymentMethod: adbrix.convertPayment(paymentMethodValue))
            
        case .viewHome:
            adbrix.commerceViewHome()
        }
    }
}
