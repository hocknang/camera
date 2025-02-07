import streamlit as st

import streamlit as st
import os

st.set_page_config(initial_sidebar_state="collapsed", menu_items=None)

st.title("Scanlah")

st.write("If you’re curious about ScanLah and want to understand how it works, be sure to watch our video!")

st.write(" In this engaging and informative presentation, we take you through an in-depth look at ScanLah’s key features, how it enhances efficiency, and the many ways it can benefit you.")

st.write("Whether you’re a new user or someone looking to maximize the platform’s potential, this video provides valuable insights into how ScanLah simplifies processes, improves user experience, and delivers practical solutions.")

st.write("Don’t miss this opportunity to see ScanLah in action and learn why it’s the perfect tool for your needs!")

video_path = "./video/scanlah.mp4"  # Use forward slashes for cross-platform compatibility
if os.path.exists(video_path):
    st.video(video_path)
else:
    st.error("Video file not found. Please check the file path.")

if st.button("Login to Scanlah"):
    st.switch_page('pages/1_Login Page.py')

st.sidebar.success("Select a page above")