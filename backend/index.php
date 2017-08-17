<?php

require 'vendor/autoload.php';
require 'AfricasTalkingGateway.php';

/** Setup **/
$USERNAME = "YOUR_USERNAME";
$API_KEY  = "YOUR_API_KEY";

$gateway = new AfricasTalkingGateway($USERNAME, $API_KEY, "sandbox");
$router = new AltoRouter();

// You can (SHOULD) implement your authentication here...


/** Send SMS **/
$router->map('POST', '/send/sms', function() {
  global $gateway;
  $message = $_POST['message'];
  $recipients = $_POST['recipients'];
  try {
    $response = $gateway->sendMessage($recipients, $message);
    echo  json_encode($response);
  } catch(AfricasTalkingGatewayException $ex) {
	  header($_SERVER["SERVER_PROTOCOL"] . ' 500 Internal Server Error', true, 500);
    echo json_encode(array(
      "status" => 500,
      "message" => $ex->getMessage()
    ));
  }
});


/** Send Airtime **/
$router->map('POST', '/send/airtime', function() {
  global $gateway;
  $recipients = $_POST['recipients'];
  try {
    $response = $gateway->sendAirtime($recipients);
    echo  json_encode($response);
  } catch(AfricasTalkingGatewayException $ex) {
	  header($_SERVER["SERVER_PROTOCOL"] . ' 500 Internal Server Error', true, 500);
    echo json_encode(array(
      "status" => 500,
      "message" => $ex->getMessage()
    ));
  }
});


/** Process Request */
$match = $router->match();
if( $match && is_callable( $match['target'] ) ) {
	call_user_func_array( $match['target'], $match['params'] ); 
} else {
	// no route was matched
	header($_SERVER["SERVER_PROTOCOL"] . ' 404 Not Found', true, 404);
}
