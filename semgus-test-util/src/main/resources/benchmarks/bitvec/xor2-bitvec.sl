;;; Metadata
(set-info :format-version "2.0.0")
(set-info :author("Jinwoo Kim" "Keith Johnson" "Wiley Corning" "Boying Li"))
(set-info :realizable true)

;;;
;;; Term types
;;;
(declare-term-types
 ;; Nonterminals
 ((B 0))

 ;; Productions
 ((($x) ; B productions
    ($y)
    ($not ($not_1 B))
    ($and ($and_1 B) ($and_2 B))
    ($or ($or_1 B) ($or_2 B)))))

;;;
;;; Semantics
;;;
(define-funs-rec
 ((B.Sem ((bt B) (x (_ BitVec 8)) (y (_ BitVec 8)) (r (_ BitVec 8))) Bool))
 ((! (match bt ; B.Sem definitions
            (($x (= r x))
              ($y (= r y))
              (($not bt1)
                (exists ((r1 (_ BitVec 8)))
                        (and
                         (B.Sem bt1 x y r1)
                         (= r (bvnot r1)))))
              (($and bt1 bt2)
                (exists ((r1 (_ BitVec 8)) (r2 (_ BitVec 8)))
                        (and
                         (B.Sem bt1 x y r1)
                         (B.Sem bt2 x y r2)
                         (= r (bvand r1 r2)))))
              (($or bt1 bt2)
                (exists ((r1 (_ BitVec 8)) (r2 (_ BitVec 8)))
                        (and
                         (B.Sem bt1 x y r1)
                         (B.Sem bt2 x y r2)
                         (= r (bvor r1 r2)))))))
     :input (x y) :output (r))))

;;;
;;; Function to synthesize - a term rooted at B
;;;
(synth-fun xor2() B) ; Using the default universe of terms rooted at B

;;;
;;; Constraints - examples
;;;
(constraint (B.Sem xor2 #b11111111 #b11111111 #b00000000))
(constraint (B.Sem xor2 #b10110000 #b01001111 #b11111111))
(constraint (B.Sem xor2 #b01000100 #b11111111 #b10111011))
(constraint (B.Sem xor2 #b11110000 #b10101010 #b01011010))

;;;
;;; Instruct the solver to find xor
;;;
(check-synth)
