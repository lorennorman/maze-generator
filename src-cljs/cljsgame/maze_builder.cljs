(ns cljsgame.maze-builder)

(defn add-chrome [game]
  (def text "Mazerator")
  (def style #js { :font "40px Arial" :fill "#ff0044" :align "center" })

  (js/game.add.text (- game.world.centerX 300) 20 text style)
)

(def maze-width 25)
(def maze-height 12)

(def all-directions [ :north :south :east :west ])

(defn y-shift [direction]
  (cond
    (= direction :north) -1
    (= direction :south)  1
    :else 0 ))

(defn x-shift [direction]
  (cond
    (= direction :east)  1
    (= direction :west) -1
    :else 0 ))

(defn rand-cell []
  { :x     (rand-int maze-width)
    :y     (rand-int maze-height)
    :walls (atom all-directions) })

; pick a strategy for growing a maze
; initialize the cell list with a random cell
(def maze
  (atom [(rand-cell)]))

(def cell-list (atom @maze))

(defn get-cells [] @maze)

(defn direction-from-to [cell neighbor]
  (let [x-offset (- (neighbor :x) (cell :x))
        y-offset (- (neighbor :y) (cell :y))]
    (cond
      (> x-offset 0) :east
      (< x-offset 0) :west
      (> y-offset 0) :south
      (< y-offset 0) :north )))

(defn opposite-direction-of [direction]
  (cond
    (= direction :south) :north
    (= direction :north) :south
    (= direction :east)  :west
    (= direction :west)  :east))

(defn tunnel-to-neighbor [cell neighbor]
  ; remove the walls between the cells
  (let [direction-of-neighbor (direction-from-to cell neighbor)
        direction-of-cell (opposite-direction-of direction-of-neighbor)]
    (swap! (cell :walls) (partial remove #{= direction-of-neighbor %}))
    (swap! (neighbor :walls) (partial remove #{= direction-of-cell %})))
  
  ; add the new neighbor to the maze
  (swap! maze conj neighbor)
  ; add the new neighbor to the cells list
  (swap! cell-list conj neighbor))

(defn delete-cell [cell]
  (swap! cell-list #(into [] (remove #{= cell %} %)))
)

(defn get-neighbor-cell [cell direction]
  { :x (+ (cell :x) (x-shift direction))
    :y (+ (cell :y) (y-shift direction))
    :walls (atom all-directions) }
)

(defn in-bounds? [cell]
  (and
    (<  (cell :x) maze-width)
    (>= (cell :x) 0)
    (<  (cell :y) maze-height)
    (>= (cell :y) 0)))

(defn not-occupied? [cell]
  (not-any?
    (fn [el]
      (and
        (= (el :x) (cell :x))
        (= (el :y) (cell :y))))
    @maze))

(defn available-neighbors [cell]
  (filter not-occupied? ; bail if the cell is out of bounds
    (filter in-bounds? ; bail if there is already a cell there in the maze
      (map
        (partial get-neighbor-cell cell) all-directions))))

(defn step []
  ; pick a cell with our chosen strategy
  (if-let [cell (last @cell-list)]
    ; get a random, valid neighbor
    (if-let [neighbor (first (shuffle (available-neighbors cell)))]
      (tunnel-to-neighbor cell neighbor)
      (delete-cell cell)))

  ; (prn (map #(vector (get % :x) (get % :y)) @cell-list))
)
