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
    </head>
    <body>
        <h1 style="text-align: center;">Barcode Scanner</h1>
    </body>
    </html>
    """

    # Embed the HTML code in the Streamlit app
    st.components.v1.html(html_code, height=700, width=700)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
