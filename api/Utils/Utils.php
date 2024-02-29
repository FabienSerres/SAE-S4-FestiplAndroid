<?php

require("View.php");

function sendJson(int $code, mixed $data): void {
    $view = new View("view");

    $view->setVar("status", $code);
    $view->setVar("json", $data);

    $view->render();
}