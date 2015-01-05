(ns cljsgame.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn index-page []
  (html5
    [:head
      [:title "Hello World"]
      (include-js "/js/main.js")
      (include-js "/js/phaser.min.js")]
    [:body {:style "margin:0; padding:0;"}
      [:div {:id "phaser-canvas" :style "margin:0; padding:0;"}]]
      [:button {:id "step"} "Step"]))
