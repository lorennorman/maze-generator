(ns cljsgame.hello)

(enable-console-print!)

(defn create []
  (cljsgame.maze-builder.add-chrome cljsgame.hello.game))

(defn update []
  (cljsgame.maze-builder.step))

(defn render []
  (doseq [cell (cljsgame.maze-builder.get-cells)]
    (cljsgame.hello.draw-cell cell)))

(defn draw-cell [cell]
  (if (has-north-wall cell)
    (js/cljsgame.hello.game.debug.geom (north-line cell)))
  (if (has-south-wall cell)
    (js/cljsgame.hello.game.debug.geom (south-line cell)))
  (if (has-east-wall cell)
    (js/cljsgame.hello.game.debug.geom (east-line cell)))
  (if (has-west-wall cell)
    (js/cljsgame.hello.game.debug.geom (west-line cell))))

(def cell-size 50)
(defn top    [cell] (*      (cell :y)  cell-size))
(defn left   [cell] (*      (cell :x)  cell-size))
(defn right  [cell] (* (inc (cell :x)) cell-size))
(defn bottom [cell] (* (inc (cell :y)) cell-size))

(defn includes? [collection element]
  (some #(= element %) collection))

(defn has-north-wall [cell]
  (includes? @(cell :walls) :north))

(defn has-south-wall [cell]
  (includes? @(cell :walls) :south))

(defn has-east-wall [cell]
  (includes? @(cell :walls) :east))

(defn has-west-wall [cell]
  (includes? @(cell :walls) :west))
  
(defn north-line [cell]
  (js/Phaser.Line. (left  cell)
                   (top   cell)
                   (right cell)
                   (top   cell)))

(defn south-line [cell]
  (js/Phaser.Line. (left   cell)
                   (bottom cell)
                   (right  cell)
                   (bottom cell)))

(defn east-line [cell]
  (js/Phaser.Line. (right  cell)
                   (top    cell)
                   (right  cell)
                   (bottom cell)))

(defn west-line [cell]
  (js/Phaser.Line. (left   cell)
                   (top    cell)
                   (left   cell)
                   (bottom cell)))

(def game-object #js{ :create create :render render :update update })

(defn on-ready [event]
  (def game (js/Phaser.Game. 1280 600 Phaser.CANVAS "phaser-canvas" game-object)))
  ; click anywhere to carve this maze
  (js/document.addEventListener "click" cljsgame.maze-builder.step)

(js/document.addEventListener "DOMContentLoaded" on-ready)
