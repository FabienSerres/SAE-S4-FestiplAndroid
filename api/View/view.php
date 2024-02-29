<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

http_response_code($status);
echo json_encode($json, JSON_UNESCAPED_UNICODE);

die;