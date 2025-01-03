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

    if st.button("Click Me"):
        st.write("Hello, World!")
        camera = st.camera_input("Capture an image")

        if camera:
            st.image(camera, caption="Captured Image")
        else:
            st.warning("No image captured yet.")

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
