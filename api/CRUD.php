<?php

class CRUD{
    private $con;

    function __construct() {
        $this->con = mysqli_connect("localhost","root", "","users");
      
    }


    public function mCreateUser($uname, $uphone, $ugender, $uaddress) {
        $sql = "INSERT INTO user(name, phone, gender, address) VALUES ('$uname',$uphone,$ugender,'$uaddress') ";
    
        $res = mysqli_query($this->con, $sql);
        if ($res) {
            return 1;
        }else {
            return 0;        
        }
        echo json_encode($response);
        
    }
    
    public function isUserExist($phone){
        $res = mysqli_query($this->con, "SELECT id FROM user WHERE phone = $phone");
        return $res;
    }
    public function mGetAllData() {
        $sql = "SELECT * FROM user";
        $res = mysqli_query($this->con, $sql);
        return $res;
    }
}



