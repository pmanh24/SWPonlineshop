<%-- 
    Document   : contact
    Created on : Nov 5, 2024, 3:47:36 PM
    Author     : OS
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Properties" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <title>Contact Us - Online Shop</title>
    <style>
        body {
            background-color: #f8f9fa;
        }

        .contact-header {
            margin-bottom: 40px;
            text-align: center;
        }

        .contact-info {
            margin-bottom: 30px;
        }
    </style>
</head>

<body id="page-top">
    <div class="container mt-5">
        <h1 class="contact-header">Contact Us</h1>
        <div class="contact-info">
            <h5>Email:</h5>
            <p><a href="mailto:onlineshopsystem95@gmail.com">onlineshopsystem95@gmail.com</a></p>
            <h5>Address:</h5>
            <p>Hanoi, Vietnam</p>
        </div>

        <div class="card">
            <div class="card-body">
                <form action="processContact.jsp" method="post">
                    <div class="form-group">
                        <label for="name">Name</label>
                        <input type="text" class="form-control" id="name" name="name" placeholder="Enter your name" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" class="form-control" id="email" name="email" placeholder="Enter your email" required>
                    </div>
                    <div class="form-group">
                        <label for="message">Message</label>
                        <textarea class="form-control" id="message" name="message" rows="4" placeholder="Type your message here..." required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Send Message</button>
                </form>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>

</html>