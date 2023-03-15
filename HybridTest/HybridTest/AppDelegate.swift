//
//  AppDelegate.swift
//  HybridTest
//
//  Created by Jimmy.강세훈 on 2023/03/14.
//

import UIKit
import AdBrixRmKit
import OSLog

@main
class AppDelegate: UIResponder, UIApplicationDelegate, AdBrixRMLogDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        let adBrix = AdBrixRM.getInstance
        
        adBrix.initAdBrix(appKey: "RruMWZd2WkOnGtPSltICMw", secretKey: "kdQnJTpBuUSj9sHzjAF7xA")
        
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }

    func didPrintLog(level: AdBrixRM.AdBrixLogLevel, log: String) {
        os_log(.debug, "%@", log)
    }

}

