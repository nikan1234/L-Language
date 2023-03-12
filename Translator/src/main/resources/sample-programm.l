function sqr(x) {
   print(x);
   return x * x;
}

function makeList(x, y) {
  l1 := [x];
  return cons(y, l1);
}

function check(a, b) {
  print(a, b);
  return a * b;
}

function firstPlusLast(list) {
    last := len(list) - 1;
    print(list);
    return nth(list, 0) + nth(list, last);
}


gamma := cond(forall x in [1, 2, 3, 5] exists y in makeList(0, 1) check(x, y) = 0 ==> [1000], [0]);
print(gamma);

shift := 1;
beta := cond(exists x subseteq [0, 1, 2, 3, 4, 6, 6] firstPlusLast(x) = len(x) ==> x, []);
print(beta);