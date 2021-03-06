(ns fhir-diff.core
  (:require [compojure.core :refer [wrap-routes defroutes GET POST routes context]]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response]]
            [ring.adapter.jetty :as jetty]
            [ring.logger :refer [wrap-with-logger]]
            [fhir-diff.controllers.api.diff :refer [diff-handler]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.json :refer [wrap-json-response]])
  (:gen-class))


(defn api-routes []
  (routes
   (POST "/diff" {:keys [params]} (diff-handler params))))

(defroutes app-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (context "/api" [] (api-routes))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-with-logger
      wrap-multipart-params
      wrap-json-response))

(def dev-handler (-> #'app wrap-reload))

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))
