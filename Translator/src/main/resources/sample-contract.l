class TestContract {
   private number;

   public function printValue() {
     print(this.number);
   }

   public function incrementValue() {
     this.number := this.number + 1;
   }
}