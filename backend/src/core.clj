(ns core
  (:require 
    [reitit.ring :as ring]
    [keycloak.deployment :as kc-deploy :refer [keycloak-client client-conf]]
    [keycloak.middleware :as middleware]
    [keycloak.authn :refer [authenticate auth-cookie]] 
    [keycloak.admin :as admin :refer [list-roles]]
    [clj-http.client :as http] ;;n'arrive pas à accéder directement à kc
    [clojure.string :as str]
    [clojure.tools.logging :as log :refer [info error]]
    [kcconfig :as kcconfig :refer [keycloak-deployment kc-client]]
    [cheshire.core :as json]))
  
(defn handler-not-protected [request]
  {:status  200 
   :body (json/generate-string {:status "not-protected"})})
   
(defn handler-protected [request]
  {:status  200 
   :body (json/generate-string {:status "protected"})})

(defn authenticated?-fn 
  ([req-method req-uri]
    (if (str/includes? req-uri "/protected") 
      true
      false))
  ([req]
    (if (str/includes? (:uri req) "/protected") 
      true
      false)))
      
(defn extract-roles-fn [method uri]
  (if (= ":get" (str method))
   ;; (admin/list-roles kc-client "app-realm") error 401
    [:manager :employee]
    [:manager])
 ;;ou get-authorization-resource  [^org.keycloak.admin.client.Keycloak keycloak-client realm-name client-id] de authz
)

(def app
  (ring/ring-handler
    (ring/router
       ["/auth" {:middleware [[middleware/wrap-authorization authenticated?-fn extract-roles-fn]]}
       ["/protected" {:get handler-protected}]
       ["/nonprotected" {:get handler-not-protected}]])
    nil 
    {:middleware [[middleware/wrap-authentication keycloak-deployment authenticated?-fn] ]}))

(defn -main []
(print "main"))

