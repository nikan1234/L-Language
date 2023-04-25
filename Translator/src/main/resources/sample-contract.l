class TestContract {
   private balances;

   function __constructor__() {
      this.balances := [1000, 0, 0, 0, 0, 0];
   }

   public function transferMoney(from, to, money) {
     enough_money := cond(nth(this.balances, from) >= money ==> 1, print(nil));

     this.balances := cond(enough_money = 1 ==> set(this.balances, from, nth(this.balances, from)  - money), this.balances);
     this.balances := cond(enough_money = 1 ==> set(this.balances, to, nth(this.balances, to) + money), this.balances);
   }

   public function printBalance(client) {
      print(nth(this.balances, client));
   }
}