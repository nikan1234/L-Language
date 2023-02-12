class Base {}

class Derived extends Base {
  public a;
  private b;
  protected c;
}

function bar(p) {
  u := p + 1;
  return u;
}


function foo(x){
  y := x * x;
  return y + bar(x);
}

rr1 := [foo(1), foo(2), foo(3)];
print(rr1);
rr1 := cons(200, cons(100, rr1));
print(rr1);

v0 := nth(rr1, 0);
v1 := nth(rr1, 1);
v2 := nth(rr1, 2);
v3 := nth(rr1, 3);
v4 := nth(rr1, 4);

print(nil);

print(v0);
print(v1);
print(v2);
print(v3);
print(v4);

print(cons([1, 2], [3, 4, 5]));

