class TestContract {
   private balances;

   function __constructor__() {
      this.balances := [1000, 0, 0, 0, 0, 0];
   }

   public function transferMoney(from, to, money) {
     this.balances := cond(nth(this.balances, from) >= money ==> function () {
                              balances := this.balances;
                              balances := set(balances, from, nth(balances, from)  - money);
                              balances := set(balances, to, nth(balances, to) + money);
                              return balances;
                           }, this.balances);
   }

   public function printBalance(client) {
      print(nth(this.balances, client));
   }
}
