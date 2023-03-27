//
//  AdBrixJavaInterface.m
//  HybridTestObjc
//
//  Created by Jimmy.강세훈 on 2023/03/21.
//

#import "AdBrixJavascriptInterface.h"

@implementation AdBrixJavascriptInterface

+ (void)event:(id)message {
    NSDictionary *json = (NSDictionary *)message;
    
    [self actionHandler:json];
}

+ (void)actionHandler:(NSDictionary *)json {
    AdBrixRM *adBrix = [AdBrixRM sharedInstance];
    NSString *method = json[@"method_name"];
    
    if ([method isEqualToString:@"login"]) {
        NSString *userId = json[@"user_id"];
        
        [adBrix loginWithUserId:userId];
    }
    else if ([method isEqualToString:@"logout"]) {
        [adBrix logout];
    }
    else if ([method isEqualToString:@"signUp"]) {
        int channelValue = [json[@"sign_channel"] intValue];
        NSDictionary *properties = json[@"sign_up_properties"];
        NSString *userId = properties[@"user_id"];
        int gender = [properties[@"gender"] intValue];
        int age = [properties[@"age"] intValue];
        
        AdBrixRmAttrModel *attr = [AdBrixRmAttrModel new];
        
        [attr setAttrDataString:@"user_id" :userId];
        [attr setAttrDataInt:@"gender" :gender];
        [attr setAttrDataInt:@"age" :age];
        
        [adBrix commonSignUpWithAttrWithChannel:channelValue commonAttr:attr];
        
    }
    else if ([method isEqualToString:@"purchase"]) {
        NSString *orderId = json[@"order_id"];
        double orderSales = [json[@"order_sales"] doubleValue];
        double discount = [json[@"discount"] doubleValue];
        double deliveryCharge = [json[@"delivery_charge"] doubleValue];
        NSInteger paymentMethodValue = [json[@"payment_method"] integerValue];
        NSArray *items = json[@"items"];

        NSMutableArray<AdBrixRmCommerceProductModel *> *productInfo = [NSMutableArray array];

        for (NSDictionary *item in items) {
            AdBrixRmCommerceProductModel *productModel = [AdBrixRmCommerceProductModel new];
            NSString *productId = item[@"product_id"];
            NSString *productName = item[@"product_name"];
            double price = [item[@"price"] doubleValue];
            NSInteger quantity = [item[@"quantity"] integerValue];
            double itemDiscount = [item[@"discount"] doubleValue];
            NSNumber *currencyNum = item[@"currency"];
            NSDictionary<NSString *, NSString *> *categoryString = item[@"categories"];
            
            NSString *currencyString = [adBrix getCurrencyString:[currencyNum integerValue]];
            
            AdBrixRmCommerceProductCategoryModel *category = [AdBrixRmCommerceProductCategoryModel new];
            [category setModelWithCategoryArr:[categoryString allValues]];
            
            AdBrixRmCommerceProductModel *product = [productModel setModelWithProductId:productId productName:productName price:price quantity:quantity discount:itemDiscount currencyString:currencyString categories:category productAttrsMap:nil];
            
            if (product != nil) {
                [productInfo addObject:product];
            }
        }
        [adBrix commonPurchaseWithOrderId:orderId productInfo:productInfo orderSales:orderSales discount:discount deliveryCharge:deliveryCharge paymentMethod:[adBrix convertPayment:paymentMethodValue]];
        
    }
    else if ([method isEqualToString:@"viewHome"]) {
        [adBrix commerceViewHome];
    }
}

@end
