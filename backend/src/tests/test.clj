(ns tests.test
  (:require 
    [reitit.ring :as ring]
    [keycloak.deployment :as kc-deploy :refer [keycloak-client client-conf]]
    [keycloak.middleware :as middleware]
    [keycloak.authn :refer [authenticate auth-cookie]] 
    [clj-http.client :as http]
    [clojure.string :as str]
    [clojure.test :as t :refer [deftest testing is use-fixtures run-tests]]
    [clojure.tools.logging :as log :refer [info error]]
    [org.httpkit.server :refer [run-server]]
    [kcconfig :as kc-config :refer [clientconf]]
    [core :as core :refer [app]]
    [cheshire.core :as json]))
    ;;unable to require from keycloak-clojure

(defonce server (atom nil))

(deftest testbackend 
  (println "Server started")
  (reset! server (run-server app {:port 4000}))
  (let [url "http://localhost:4000/auth/protected"]
  (try
    (testing "get request for protected endpoint"
      (let [
         token (authenticate {:auth-server-url "http://localhost:8090/auth" :realm "app-realm" :client-id "app-backend" } "testaccount" "password" )
          ;;token (authenticate clientconf "testaccount" "password" ) --> causes invalid client credentials 
          result (http/get url {:cookies       (auth-cookie token)
                                :content-type  "application/json"
                                :accept        "application/json"
                                            })
          json-response (json/parse-string (:body result) true)
        ]
          (and (is (> (count result) 0)) (= "protected" (:status json-response)))))
    (testing "Request on protected endpoint without token should give back an error"
        (is (thrown? Exception (http/get url))))
     ;; (finally
      ;;  ((when-not (nil? @server)
       ;;    (@server :timeout 100)
        ;;   (reset! server nil))))
           )))

(run-tests 'tests.test)
