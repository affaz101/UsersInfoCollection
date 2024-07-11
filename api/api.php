<?php 
include 'CRUD.php';


//$conn = mysqli_connect("localhost","root", "", "users");
$response = array();
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['u_name']) and isset($_POST['u_phone']) and  isset($_POST['u_gender']) and  isset($_POST['u_address']))  {
        // $response['error'] = false;
        // $response['message'] = 'all input is ok';

        // mCreateUser( $_POST['u_name'], $_POST['u_phone'], $_POST['u_gender'], $_POST['u_address'] );

        $name = $_POST['u_name'];
        $phone = $_POST['u_phone'];
        $gender = $_POST['u_gender'];
        $address = $_POST['u_address'];

        // $response['message'] = "ki holo $name, $phone, $gender, $address";
        // echo json_encode($response);



        $db = new CRUD();
        $aaa = $db->isUserExist($_POST['u_phone']);
        $r = mysqli_fetch_assoc($aaa);
        if ($r) {
            $response['error'] = true;
            $response['message'] = 'User allready Exist';
            echo json_encode($response);
        }else {
            
            
            $res = $db->mCreateUser( $_POST['u_name'], $_POST['u_phone'], $_POST['u_gender'], $_POST['u_address'] );
            if($res == 1){
                $response['error'] = false;
                $response['message'] = 'User Registered Successfully';
            }
            
            echo json_encode($response);
        }


    }else {
        $response['error'] = true;
        $response['message'] = 'missing';
        echo json_encode($response);
    }
    
}elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['all'])) {
        $db = new CRUD();
        $res = $db->mGetAllData();
        $arr = [];
        $i=0;
        if ($res) {
            while ($r = mysqli_fetch_assoc($res)) {
                $arr[$i] = array_merge($r);
                $i++;
            }
            $response['error'] = false;
            $response['message'] = 'Data Retrived Successfully';

        }else {
            $response['error'] = true;
            $response['message'] = 'Some problem occurse';        
        }
        
        // echo json_encode($response);
        echo json_encode($arr);

    }
    // var_dump($_GET['all']);
} else {
    print("not");
}












// mysqli_close($conn);















?>