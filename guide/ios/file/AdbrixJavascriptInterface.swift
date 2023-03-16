//
//  AdbrixJavascriptInterface.swift
//  HybridTest
//
//  Created by Jimmy.강세훈 on 2023/03/14.
//

import Foundation
import AdBrixRmKit

struct AdbrixJavascriptInterface {
    
    static func event(message: Any) {
        guard let json = message as? [String: Any],
              let action = json["method_name"] as? String else {return}
  
        guard let event = AdbrixActionList(rawValue: action) else {return}
  
        do {
            try event.invoke(json: json)
        } catch {
            print(error)
        }
    }
    
    enum AdbrixActionList: String, Error {
        case login
        case logout
        case signUp
        case purchase
        case viewHome
        
        func invoke(json: [String: Any]) throws {
            let adbrix = AdBrixRM.sharedInstance()
            
            switch self {
                
            case .login:
                guard let userId = json["user_id"] as? String else {throw self}
                adbrix.login(userId: userId)
                
            case .logout:
                adbrix.logout()
                
            case .signUp:
                guard let channelValue = json["sign_channel"] as? Int,
                      let channel = AdBrixRM.AdBrixRmSignUpChannel(rawValue: channelValue),
                      let properties = json["sign_up_properties"] as? [String: Any],
                      let userId = properties["user_id"] as? String,
                      let gender = properties["gender"] as? Int,
                      let age = properties["age"] as? Int else {throw self}
                
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
                      let items = json["items"] as? NSArray else {throw self}
                
                
                let productInfo = items.compactMap { value -> AdBrixRmCommerceProductModel? in
                    let productModel = AdBrixRmCommerceProductModel()
                    guard let map = value as? [String: Any] else {return nil}
                    
                    guard let productId = map["product_id"] as? String,
                          let productName = map["product_name"] as? String,
                          let price = map["price"] as? Double,
                          let quantity = map["quantity"] as? Int,
                          let discount = map["discount"] as? Double,
                          let currencyNum = map["currency"] as? Int?,
                          let categoryString = map["categories"] as? [String:String]?
                    else {return nil}
                    
                    let currencyString = currencyNum.map{adbrix.getCurrencyString($0)}
                    let category = categoryString.map{ AdBrixRmCommerceProductCategoryModel().setModel(categoryArr: $0.compactMap {$1}) }
                    
                    return productModel.setModel(productId: productId, productName: productName, price: price, quantity: quantity, discount: discount ,currencyString: currencyString, categories: category, productAttrsMap: nil)
                }
                
                adbrix.commonPurchase(orderId: orderId, productInfo: productInfo, orderSales: orderSales ,discount: discount, deliveryCharge: deliveryCharge, paymentMethod: adbrix.convertPayment(paymentMethodValue))
                
            case .viewHome:
                adbrix.commerceViewHome()
            }
        }
        
    }
}
