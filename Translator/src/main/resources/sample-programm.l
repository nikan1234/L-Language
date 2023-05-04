function check(a, b) {
  print(a, b);
  return a * b;
}


class A {
   public x;
   private y;

   public function foo() {
      print(this.y);
   }

   __constructor__(x, y) {
        this.x := check(x, y);
        this.y := y;
        this.foo();
   }
}


function sqr(x) {
   print(x);
   return x * x;
}

function makeList(x, y) {
  l1 := [x];
  return cons(y, l1);
}


function firstPlusLast(list) {
    last := len(list) - 1;
    print(list);
    return nth(list, 0) + nth(list, last);
}


gamma := cond(forall x in [1, 2, 3, 5] exists y in makeList(0, 1) check(x, y) = 0 ==> [1000], [0]);
print(gamma);

shift := 1;
beta := cond(exists x subseteq [0, 1, 2, 3, 4, 6, 6] firstPlusLast(x) = len(x) ==> do {
                print(1234);
                shift := shift + 1;
                return shift;
             },
             []);

print(beta);
print(shift);

w := iteration<10>(x := nil, len(x) <> 9 ==> do {print(x); return cons(x, x);});
print(w);
