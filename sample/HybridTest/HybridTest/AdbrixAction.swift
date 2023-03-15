//
//  AdbrixAction.swift
//  HybridTest
//
//  Created by Jimmy.강세훈 on 2023/03/14.
//

import Foundation
import AdBrixRmKit

enum AdBrixAction: String {
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
                  let price = json["price"] as? Double,
                  let paymentMethodValue = json["payment_method"] as? Int,
                  let currencyValue = json["currency"] as? Int,
                  let quantity = json["quantity"] as? Int,
                  let items = json["items"] as? [String: Any],
                  let categoryJson = json["categories"] as? [String: Any] else {return}
            
            var categoryValue = [String]()
            
            for (_, value) in categoryJson {
                guard let value = value as? String else {return}
                categoryValue.append(value)
            }
            
            let category = AdBrixRmCommerceProductCategoryModel().setModel(categoryArr: categoryValue)
            
            let currency = adbrix.getCurrencyString(currencyValue)

            let productModel = adbrix
                .createCommerceProductDataWithAttr(productId: productId,
                                                   productName: productName,
                                                   price: price,
                                                   quantity: quantity,
                                                   discount: 0,
                                                   currencyString: currency,
                                                   category: category,
                                                   productAttrsMap: nil)

            adbrix.commonPurchase(orderId: orderId,
                                  productInfo: [productModel],
                                  discount: 0,
                                  deliveryCharge: 0,
                                  paymentMethod: .init(rawValue: paymentMethodValue) ?? .ETC)
        
            
        case .viewHome:
            adbrix.commerceViewHome()
            
        }
    }
}
