(ns 
  ^{:author "mikera"
    :doc "Core clisk image generation functions"}
  clisk.core
  (:import clisk.Util)
  (:import [java.awt.image BufferedImage])
  (:import [mikera.gui Frames])
  (:import [javax.swing JComponent])
  (:use [mikera.cljutils core])
  (:require [clojure test])
  (:require [mikera.image.core :as imagez])
  (:use [clisk node functions util]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(def ^:dynamic *anti-alias* 2)

(defn sample 
  "Samples the value of a node at a given position"
  ([node] (sample node [0.0 0.0]))
  ([node pos]
    (let [node (clisk.node/node node)
          pos (vectorize pos)
          vnode (vectorize node)
          scalarnode? (scalar-node? node)
          fns (vec (map compile-fn (components vnode)))
          [x y z t] (map #(evaluate (component % pos)) (range 4))
          vals (mapv #(.calc ^clisk.IFunction % (double x) (double y) (double z) (double t)) fns)]
      (if scalarnode? (first vals) vals))))

(defn sampler 
  "Creates a sampler function for a given node, which is a fn from position to sample value"
  ([node] 
    (let [node (clisk.node/node node)
          scalarnode? (scalar-node? node)
          vnode (vectorize node)
          fns (mapv compile-fn (components vnode))]
      (fn [[x y z t]]
        (let [x (double (or x 0.0))
              y (double (or y 0.0))
              z (double (or z 0.0))
              t (double (or t 0.0))
              vals (mapv #(.calc ^clisk.IFunction % x y z t) fns)]
          (if scalarnode? (first vals) vals))))))

(defn tst [] (clojure.test/run-all-tests))

(defn scale-image 
  "Scales an image to a given width and height"
  (^BufferedImage [^BufferedImage img w h]
    (imagez/scale-image img w h)))

(defn show-comp 
  "Shows a component in a new frame"
  ([com 
    & {:keys [^String title]
       :as options
       :or {title nil}}]
  (let [^JComponent com com]
    (Frames/display com title))))

(defn vector-function 
  "Defines a vector function, operating on vectorz vectors"
  (^clisk.VectorFunction [a 
                & {:keys [input-dimensions]}]
    (let [a (vectorize a)
          input-dimensions (int (or input-dimensions 4))
          ^java.util.List funcs (mapv compile-fn (components a))]
      (clisk.VectorFunction/create input-dimensions funcs))))

(defn image
  "Creates a bufferedimage from the given clisk data"
  (^BufferedImage [cliskfn
                   & {:keys [width height size anti-alias] 
       :or {size DEFAULT-IMAGE-SIZE}}]
    (let [cliskfn (validate (node cliskfn))
          aa (double (or anti-alias *anti-alias*))
          w (int (or width size))
          h (int (or height size))
          fw (* w aa)
          fh (* h aa)
          img (img cliskfn fw fh)]
      (scale-image img w h))))

;; scale factor 
(def ^:dynamic DISPLAY-SCALE 4.0)

(defn show 
  "Creates and shows an image from the given image vector function"
  ([image-or-function
    & {:keys [width height size anti-alias scale] 
       :or {size DEFAULT-IMAGE-SIZE scale DISPLAY-SCALE}
       :as ks}]
    (let [^BufferedImage buf-img (if (instance? BufferedImage image-or-function)
                                   image-or-function
                                   (apply image image-or-function (mapcat identity ks)))
          ^BufferedImage buf-img (if scale 
                                   (scale-image buf-img 
                                          (* (imagez/width buf-img) (double scale))
                                          (* (imagez/height buf-img) (double scale)))
                                   buf-img)]
      (Util/show buf-img))))
