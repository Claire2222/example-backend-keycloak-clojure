(ns kcconfig
  (:require [keycloak.admin :as admin :refer :all]
            [keycloak.starter :as starter :refer [init!]]
            [keycloak.user :as user]
            [keycloak.deployment :as deployment :refer [deployment keycloak-client client-conf]]
            [talltale.core :as talltale :refer :all]
            [me.raynes.fs :as fs])
  (:gen-class))

;;be sure that keycloak is running on port 8090 
           
(def clientconf (client-conf {:auth-server-url "http://localhost:8090/auth"
 :admin-realm      "master"
 :realm            "app-realm"
 :admin-username   "admin"
 :admin-password   "adminpass"
 :client-admin-cli "admin-cli"
 :client-id        "app-backend"
;; :client-secret    "82d7b7f4-4a9d-49a3-86cb-7677b9247bfc"
 }))
             
             
(def keycloak-deployment (deployment clientconf))

(def kc-client (keycloak-client clientconf "admin" "secretadmin"))


;;keycloak configuration : client "my-backend" with public access-type and update password not required, in realm "app-realm" with a role "manager". User "testaccount" with password "password" and role "manager", update password not required.

;;(admin/create-realm! kc-client "app-realm")

