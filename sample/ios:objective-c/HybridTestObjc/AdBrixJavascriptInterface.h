//
//  AdBrixJavaInterface.h
//  HybridTestObjc
//
//  Created by Jimmy.강세훈 on 2023/03/21.
//

#import <Foundation/Foundation.h>
#import <AdBrixRmKit/AdBrixRmKit.h>

@interface AdBrixJavascriptInterface : NSObject

+(void)event:(id)message;
+(void)actionHandler:(NSDictionary *)json;
@end
