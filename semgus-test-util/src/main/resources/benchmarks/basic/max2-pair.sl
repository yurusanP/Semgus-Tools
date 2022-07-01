;;;;
;;;; max2-pair.sl - The max2 pair example problem encoded in SemGuS
;;;;

;;; Metadata
(set-info :format-version "2.0.0")
(set-info :author("Jinwoo Kim" "Keith Johnson" "Wiley Corning" "Boying Li" "Jack Xu"))
(set-info :realizable true)

;;;
;;; Term types
;;;
(declare-term-types
 ;; Nonterminals
 ((E 0) (B 0))

 ;; Productions
 ((($x); E productions
   ($y)
   ($0)
   ($1)
   ($+ ($+_1 E) ($+_2 E))
   ($ite($ite_1 B) ($ite_2 E) ($ite_3 E)))

  (($t) ; B productions
   ($f)
   ($not ($not_1 B))
   ($and($and_1 B) ($and_2 B))
   ($or($or_1 B) ($or_2 B))
   ($< ($<_1 E) ($<_2 E)))))

;;;
;;; Semantics
;;;
(define-funs-rec
    ;; CHC heads
    ((E.Sem ((et E) (xfi Int) (xse Int) (yfi Int) (yse Int) (rfi Int) (rse Int)) Bool)
     (B.Sem ((bt B) (xfi Int) (xse Int) (yfi Int) (yse Int) (r Bool)) Bool))

  ;; Bodies
  ((! (match et ; E.Sem definitions
       (($x (not (not (and (= rfi xfi) (= rse xse)))))
        ($y (not (not (and (= rfi yfi) (= rse yse)))))
        ($0 (not (not (and (= rfi 0) (= rse 0)))))
        ($1 (not (not (and (= rfi 1) (= rse 1)))))
        (($+ et1 et2)
         (exists ((rfi1 Int) (rse1 Int) (rfi2 Int) (rse2 Int))
             (and
              (E.Sem et1 xfi xse yfi yse rfi1 rse1)
              (E.Sem et2 xfi xse yfi yse rfi2 rse2)
              (and
              (= rfi (+ rfi1 rfi2))
              (= rse (+ rse1 rse2))))))
        (($ite bt1 etc eta)
         (exists ((r0 Bool) (rfi1 Int) (rse1 Int) (rfi2 Int) (rse2 Int))
             (and
              (B.Sem bt1 xfi xse yfi yse r0)
              (E.Sem etc xfi xse yfi yse rfi1 rse1)
              (E.Sem eta xfi xse yfi yse rfi2 rse2)
              (and
              (= rfi (ite r0 rfi1 rfi2))
              (= rse (ite r0 rse1 rse2))))))))

    :input (xfi xse yfi yse) :output (rfi rse))

   (! (match bt ; B.Sem definitions
        (($t (= r true))
         ($f (= r false))
         (($not bt1)
          (exists ((rse Bool))
              (and
               (B.Sem bt1 xfi xse yfi yse rse)
               (= r (not rse)))))
         (($and bt1 bt2)
          (exists ((rse1 Bool) (rse2 Bool))
              (and
               (B.Sem bt1 xfi xse yfi yse rse1)
               (B.Sem bt2 xfi xse yfi yse rse2)
               (= r (and rse1 rse2)))))
         (($or bt1 bt2)
          (exists ((rse1 Bool) (rse2 Bool))
              (and
               (B.Sem bt1 xfi xse yfi yse rse1)
               (B.Sem bt2 xfi xse yfi yse rse2)
               (= r (or rse1 rse2)))))
         (($< et1 et2)
          (exists ((rfi1 Int) (rfi2 Int) (rse1 Int) (rse2 Int))
              (and
               (E.Sem et1 xfi xse yfi yse  rfi1 rse1)
               (E.Sem et2 xfi xse yfi yse  rfi2 rse2)
               (= r (< (+ rfi1 rse1) (+ rfi2 rse2))))))))
    :input (xfi xse yfi yse) :output (r))))

;;;
;;; Function to synthesize - a term rooted at E
;;;
(synth-fun max2() E) ; Using the default universe of terms rooted at E

;;;
;;; Constraints - examples
;;;
(constraint (E.Sem max2 4 2 3 2 4 2))
(constraint (E.Sem max2 2 5 6 0 2 5))
(constraint (E.Sem max2 2 (+ 3 4) 1 1 2 7))

;;;
;;; Instruct the solver to find max2
;;;
(check-synth)
