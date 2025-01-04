# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Barcode Scanner with Streamlit")

    # HTML and JavaScript code
    html_code = """
    <!DOCTYPE html>
    <html>
    <head>
        <title>Barcode Scanner</title>
        <script src="https://unpkg.com/html5-qrcode@2.3.8/html5-qrcode.min.js"></script>
        <style>
            #qr-reader {
            width: 500px;
            margin: 20px auto;
        }
        #controls {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin-top: 10px;
        }
        #qr-reader-results {
            text-align: center;
            margin-top: 20px;
            font-size: 18px;
        }
    </style>
    </head>
    <body>
        <h1 style="text-align: center;">Barcode Scanner</h1>
        <div id="controls">
            <select id="camera-select"></select>
            <button id="start-scanner" disabled>Start Scanner</button>
        </div>
        <div id="qr-reader"></div>
        <div id="qr-reader-results">Scanned Code will appear here.</div>
    </body>
    </html>
    """

    # Embed the HTML code in the Streamlit app
    st.components.v1.html(html_code, height=700, width=700)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
