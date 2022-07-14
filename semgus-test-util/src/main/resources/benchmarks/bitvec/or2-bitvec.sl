(set-info :format-version "2.0.0")
(set-info :author("Jack Xu"))
(set-info :realizable true)

(declare-term-types
 ((BV 0))
 ((($x)
    ($y)
    ($bvnot ($bvnot_1 BV))
    ($bvand ($bvand_1 BV) ($bv_and_2 BV)))))

(define-funs-rec
 ((BV.Sem ((bv BV) (x (_ BitVec 8)) (y (_ BitVec 8)) (r (_ BitVec 8))) Bool))
 ((! (match bv
            (($x (= r x))
              ($y (= r y))
              (($bvnot bv1)
                (exists ((r1 (_ BitVec 8)))
                        (and
                         (BV.Sem bv1 x y r1)
                         (= r (bvnot r1)))))
              (($bvand bv1 bv2)
                (exists ((r1 (_ BitVec 8)) (r2 (_ BitVec 8)))
                        (and
                         (BV.Sem bv1 x y r1)
                         (BV.Sem bv2 x y r2)
                         (= r (bvand r1 r2)))))))
     :input (x y) :output (r))))

(synth-fun or2() BV)

(constraint (BV.Sem or2 #b00001111 #b11110000 #b11111111))
(constraint (BV.Sem or2 #b00101011 #b10110000 #b10111011))
(constraint (BV.Sem or2 #x12 #x34 #x36))
(constraint (BV.Sem or2 #xAB #xCD #xEF))

(check-synth)
