app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('/cart/findCartList.do');
    }
    //向购物车添加商品
    this.addGoodsToCartList=function (num,itemId) {
        return $http.get('/cart/addGoodsToCartList.do?num='+num+"&itemId="+itemId);
    }

    this.findAdressList=function () {
        return $http.get('/address/findAdressList.do')
    }
    
    this.submitOrder=function (order) {
        return $http.post('/order/add.do',order);
        
    }
});
