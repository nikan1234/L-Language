class Base {
   public x;
   public y;

   public __constructor__() {
      this.x := 234;
   }

   private function baz() {
      print(1);
   }


   protected function bar() {
        print(2);
        this.baz();
   }
}

class Derived extends Base {

    private r;

    public function bar() {
       print(11111111111111);
    }

    public function foo() {
        print(3);
        super.bar();
    }
}

a := new Derived();
print(a.x);
a.foo();
a.bar();
