# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Camera Capture and Upload")

    enable = st.checkbox("Enable camera")
    picture = st.camera_input("Take a picture", disabled=not enable)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
