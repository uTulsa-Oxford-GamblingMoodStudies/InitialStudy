<?php
header("Content-Type: application/xml");

include('config.php');
require_once($xmlrpc_path.'/xmlrpc.inc');
require_once($xmlrpc_path.'/xmlrpcs.inc');
include('captcha.php');


function checkUser($email, $pass){
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    
    $db_addr = $db_url . ":" . $db_port;
    $server = mysql_connect( $db_addr, $db_user, $db_pass);
    
    if($server == False)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to connect to DB");
    
    mysql_select_db($db_database);
    
    $a = mysql_real_escape_string($email);
    
    $rset = mysql_query("SELECT `uid`,`password` FROM `users` WHERE email='$a'");
    
    if($rset == False)
        return new xmlrpcresp(0, $xmlrpcerruser, "Bad Query");
    
    $uid = 0;
    if ($row = mysql_fetch_assoc($rset)) {
        if(!(md5($pass) === $row['password'])){
            return new xmlrpcresp(0, $xmlrpcerruser, "Incorrect password");
        }
        $uid = $row['uid'];
    }
    else 
        return new xmlrpcresp(0, $xmlrpcerruser, "No Such User");
    
    return $uid;
}

function post__accel_data($m){
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    $email      = $m->getParam(0)->scalarVal();
    $pass       = $m->getParam(1)->scalarVal();
    $play_id    = $m->getParam(2)->scalarVal();
    $time_stamp = $m->getParam(3)->scalarVal();
    $ax         = $m->getParam(4)->scalarVal();
    $ay         = $m->getParam(5)->scalarVal();
    $az         = $m->getParam(6)->scalarVal();
    $has_gyro   = $m->getParam(7)->scalarVal();
    $gx         = $m->getParam(8)->scalarVal();
    $gy         = $m->getParam(9)->scalarVal();
    $gz         = $m->getParam(10)->scalarVal();

    $rval = checkUser($email, $pass);
    if(!is_numeric($rval))
        return $rval;

    if(!is_numeric($play_id)
        return new xmlrpcresp(0, $xmlrpcerruser, "Play ID value must be int");
    }
 
    if(!is_numeric($ax)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Acceleration X value must be numeric");
    }
 
    if(!is_numeric($ay)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Acceleration Y value must be numeric");
    }
 
    if(!is_numeric($az)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Acceleration Z value must be numeric");
    }
 
    $result = mysql_query("INSERT INTO `accelerometer_data` (`pid`, `time_stamp`, `ax`, `ay', `az`, `has_gyro`, `gx`, `gy`, `gz`) VALUES ($play_id, $time_stamp, $ax, $ay, $az, has_gyro, $gx, $gy, $gz);");

    if($resultt == False){
        return new xmlrpcresp(0, $xmlrpcerruser, "Could not insert accelerometer data into the database.");
    }
}


function startSession($m){
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    $lucky = $m->getParam(2)->scalarVal();
    $rval = checkUser($email, $pass);
    if(!is_numeric($rval))
        return $rval;

    if(!is_numeric($lucky)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Luckiness value must be int");
    }
    
    $result = mysql_query("INSERT INTO `sessions` (`uid`, `lucky`, `control`) VALUES ($rval, $lucky, -1);");
    
    $session_id = mysql_insert_id();
    
    return new xmlrpcresp(new xmlrpcval($session_id, "string"));
}

function finalizeSession($m){
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;

    
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    $session_id = $m->getParam(2)->scalarVal();
    $control = $m->getParam(3)->scalarVal();
    
    $rval = checkUser($email, $pass);
    if(!is_numeric($rval))
        return $rval;
    
    if(!is_numeric($session_id)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Session value must be int");
    }

    if(!is_numeric($control)){
        return new xmlrpcresp(0, $xmlrpcerruser, "Control value must be int");
    }
    
    mysql_query("UPDATE `sessions` SET `control`=$control WHERE `sid`=$session_id;");
    
    return new xmlrpcresp(new xmlrpcval(1, "boolean"));
}
function score($m) {
global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    
    $rval = checkUser($email, $pass);
    if(!is_numeric($rval))
        return $rval;
    
    $result = mysql_query("SELECT SUM(winning) as total FROM `plays` WHERE winning=result AND uid='$rval';");
    
    if($result == False){
        return new xmlrpcresp(0, $xmlrpcerruser, "Could not query plays");
    }
    
    if($row = mysql_fetch_assoc($result)){
        $value = $row['total'] * 10.0;
        return new xmlrpcresp(new xmlrpcval("$value", "string"));
    }
    
    return new xmlrpcresp(0, $xmlrpcerruser, "Could not get result");
}

function elapsedPlayTime($uid){
global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port, $min_play_interval;

    $result = mysql_query("SELECT COUNT(*) as cnt FROM `plays` WHERE `uid` = $uid;");

    if(($row = mysql_fetch_assoc($result))!=null){
        if($row['cnt'] == 0){
            return $min_play_interval;
        }
    }else{
        return $min_play_interval;
    }

    $result = mysql_query("SELECT UNIX_TIMESTAMP(`timestamp`) as ptime, UNIX_TIMESTAMP(NOW()) as ntime FROM `plays` WHERE `uid` = $uid ORDER BY `timestamp` DESC;");
    
    if(!$result){
        return $min_play_interval;
    }
    
    if($row = mysql_fetch_assoc($result)){
        return $row['ntime'] - $row['ptime'];
    }
}

function play($m) {
global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port, $min_play_interval;
    $email = $m->getParam(0)->scalarVal();
    $pass  = $m->getParam(1)->scalarVal();
    $sid   = $m->getParam(2)->scalarVal();

    $rval = checkUser($email, $pass);
    if(!is_numeric($rval))
        return $rval;
    
    $time = elapsedPlayTime($rval);
    
    if($time < $min_play_interval){
        return new xmlrpcresp(0, $xmlrpcerruser, "You play too much");
    }
    $towin = rand(1,6);
    $result = rand(1,6);
    
    $rset = mysql_query("INSERT INTO `plays` (sid, winning, result) VALUES ($sid, $towin, $result);");
    $pid = mysql_insert_id();

    if($rset == False){
        return new xmlrpcresp(0, $xmlrpcerruser, "Could not create play");
    }
    
    return new xmlrpcresp(new xmlrpcval("$towin $result $pid", "string"));
}


function upload($m) {
global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port, $data_dir;
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    
    $rval = checkUser($email, $pass);

    if(!is_numeric($rval)){
        return $rval;
    }
    
    $uid = $rval;
    
    $data = $m->getParam(2)->scalarVal();
    
    // $md5 = $m->getParam(3)->scalarVal();

    // check the data against the MD5
    
    // if(!($md5 === md5($data))){
    //     return new xmlrpcresp(0, $xmlrpcerruser, "MD5 mismatch: Data corrupted in transmission?");
    // }
        
    // base64 decode
    
    $str_form = base64_decode($data);
    
    // save to uniq file $pid.$time.data
    
    $time = time();
    $result = mysql_query("SELECT `pid` FROM `plays` WHERE `uid` = $uid ORDER BY `timestamp` DESC;");
    $pid = 0;
    
    if($row = mysql_fetch_assoc($result)){
        $pid = $row['pid']; 
    }else{
        return new xmlrpcresp(0, $xmlrpcerruser, "No plays for uid $uid");
    }

    $result = mysql_query("SELECT `pid` FROM `accelerometer_data` WHERE `pid` = $pid");
    if($row = mysql_fetch_assoc($result)){
        return new xmlrpcresp(0, $xmlrpcerruser, "$pid data already uploaded");
    }
    
    $newfile="$pid.$time.data";
    $file = fopen ($data_dir.DIRECTORY_SEPARATOR.$newfile, "wb");
    fwrite($file, $str_form);
    fclose ($file);
    
    // upload pid, uid, data_file_id to database
    
    mysql_query("INSERT INTO `accelerometer_data` (`pid`,`uid`,`data_file_identifier`) VALUES ($pid, $uid, '$newfile');");
    
    return new xmlrpcresp(new xmlrpcval(1, "boolean"));
}

function login($m) {
global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    
    $rval = checkUser($email, $pass);
    
    if(!is_numeric($rval)){
        return $rval;
    }
    
    return new xmlrpcresp(new xmlrpcval(1,"boolean"));
}

function base64_encode_image ($filename=string,$filetype=string) {
    if ($filename) {
        $imgbinary = fread(fopen($filename, "r"), filesize($filename));
        return 'data:image/' . $filetype . ';base64,' . base64_encode($imgbinary);
    }
}

function request($m) {
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port, $tmp_dir;

    $db_addr = $db_url . ":" . $db_port;
    $server = mysql_connect( $db_addr, $db_user, $db_pass);
    if(!$server)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to connect to DB");
    
    mysql_select_db($db_database);
    
    
    $captcha = new SimpleCaptcha();
    $captcha->CreateImage();
    
    $c = $captcha->txt;
    $i = $captcha->im;
    $rval = mysql_query("INSERT INTO `captcha` (`answer`, `tried`) VALUES ('$c', 0)");
    if(!$rval)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to create Captcha DB entry");
    
    $session = mysql_insert_id();
    
    // convert image to base64
    
    $filename = tempnam( $tmp_dir , "cpt" );
    @imagejpeg($i, $filename);
    @imagedestroy($i);
    
    $base64img = base64_encode_image($filename, "jpeg");
    
    unlink($filename);
    
    return new xmlrpcresp(new xmlrpcval(array(new xmlrpcval("$session", "string"), new xmlrpcval($base64img, "string")), "array"));
}

function register($m) {
    global $xmlrpcerruser, $db_user, $db_pass, $db_database, $db_url, $db_port;
    
    $email = $m->getParam(0)->scalarVal();
    $pass = $m->getParam(1)->scalarVal();
    $name = $m->getParam(2)->scalarVal();
    $captcha = $m->getParam(3)->scalarVal();
    $session = $m->getParam(4)->scalarVal();
    
    $db_addr = $db_url . ":" . $db_port;
    $server = mysql_connect( $db_addr, $db_user, $db_pass);
    if(!$server)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to connect to DB");
    
    mysql_select_db($db_database);

/*    
    // validate captcha code
    if(!is_numeric($session))
        return new xmlrpcresp(0, $xmlrpcerruser, "Session id not numeric");
    
    $result = mysql_query("SELECT `answer`, `tried` FROM `captcha` WHERE `session_id` = $session;");

    if(!$result)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to query Captcha DB");
    
    if($row = mysql_fetch_assoc($result)){
        if($row['tried'] == 1){
            return new xmlrpcresp(0, $xmlrpcerruser, "Bad session ID");
        }
        if($row['answer'] != $captcha){
            mysql_query("UPDATE `captcha` SET `tried` = 1 WHERE `session_id` = $session;");
            return new xmlrpcresp(0, $xmlrpcerruser, "Incorrect Captcha Code");
        }
    }else{
        return new xmlrpcresp(0, $xmlrpcerruser, "Bad session ID");
    }

    mysql_query("UPDATE `captcha` SET `tried` = 1 WHERE `session_id` = $session;");
    */
    $error = "";

    // check password complexity requirements
    if( strlen($pass) > 20 ) {
	    $error .= "Password too long ( > 20 characters)!\n";
    }
    if( strlen($pass) < 8 ) {
	    $error .= "Password too short ( < 8 characters)!\n";
    }
    if( !preg_match("#[0-9]+#", $pass) ) {
	    $error .= "Password must include at least one number!\n";
    }
    if( !preg_match("#[a-z]+#", $pass) ) {
	    $error .= "Password must include at least one letter!";
    }
    
    if( $error != "" ){
        return new xmlrpcresp(0, $xmlrpcerruser, $error);
    }
    
    // check user exists
    $a = mysql_real_escape_string($email);
    $b = md5($pass);
    $c = mysql_real_escape_string($name);
    
    $rset = mysql_query("SELECT COUNT(*) as cnt FROM `users` WHERE email='$a';");

    if(!$rset)
        return new xmlrpcresp(0, $xmlrpcerruser, "Bad Query");
    
    $quantity = 0;
    
    if ($row = mysql_fetch_assoc($rset)) {
        $quantity = $row['cnt'];
    }
    else {
        return new xmlrpcresp(0, $xmlrpcerruser, "Bad Result For Existence Query");
    }
    
    if(quantity > 0)
        return new xmlrpcresp(0, $xmlrpcerruser, "Username in Use");
    
    if(mysql_query("INSERT INTO `users` (`email`,`password`,`realname`) VALUES ('$a', '$b', '$c');") == False)
        return new xmlrpcresp(0, $xmlrpcerruser, "Unable to insert");
    
    return new xmlrpcresp(new xmlrpcval(1,"boolean"));
}

$a = array( "startSession" => array( "function" => "startSession" ),
            "finalizeSession" => array( "function" => "finalizeSession" ),
            "login" => array( "function" => "login" ),
            "play" => array( "function" => "play" ),
            "register" => array( "function" => "register" ),
            "requestCode" => array( "function" => "request" ),
            "score" => array( "function" => "score" ),
            "upload" => array( "function" => "upload"));

$s = new xmlrpc_server($a, false);
#$s->setdebug(3);
$s->service();
?>
