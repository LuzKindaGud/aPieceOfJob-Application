import favicon from '../../frontend/src/assets/image/favicon.png';
export const getOTPEmailTemplate = (otp) => {
    return `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực tài khoản - aPieceOfJob</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 20px auto;
            background-color: #ffffff;
            border-radius: 0px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: black;
            color: #ffffff;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 600;
        }
        .content {
            padding: 40px 30px;
            color: #333333;
            text-align: center;
        }
        .content p {
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 20px;
        }
        .otp-box {
            background-color: #f8f9fa;
            border: 2px dashed #c31432;
            border-radius: 8px;
            padding: 20px;
            margin: 30px 0;
            display: inline-block;
        }
        .otp-code {
            font-size: 36px;
            font-weight: bold;
            color: #000000ff;
            letter-spacing: 5px;
            margin: 0;
        }
        .footer {
            background-color: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #888888;
            font-size: 12px;
            border-top: 1px solid #eeeeee;
        }
        .footer p {
            margin: 5px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1 style="display:flex;align-items:center;justify-content:center;gap:10px;margin:0;">
                <img src="${favicon}" alt="aPieceOfJob" style="height:40px;width:40px;border-radius:8px;object-fit:cover;display:inline-block;vertical-align:middle;" />
                <span style="display:inline-block;vertical-align:middle;">aPieceOfJob</span>
            </h1>
        </div>
        <div class="content">
            <h2>Xác thực tài khoản của bạn</h2>
            <p>Xin chào,</p>
            <p>Cảm ơn bạn đã đăng ký tài khoản tại aPieceOfJob. Để hoàn tất quá trình đăng ký, vui lòng sử dụng mã xác thực (OTP) dưới đây:</p>
            
            <div class="otp-box">
                <p class="otp-code">${otp}</p>
            </div>
            
            <p>Mã này sẽ hết hạn sau <strong>10 phút</strong>.</p>
            <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>
        </div>
        <div class="footer">
            <p>&copy; 2025 aPieceOfJob. All rights reserved.</p>
            <p>Đây là email tự động, vui lòng không trả lời.</p>
        </div>
    </div>
</body>
</html>
    `;
};
