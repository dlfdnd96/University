"""
비디오 재생 프로그램
@Author: 류일웅
@Since: 2019-05-13
"""
import cv2
import os
import sys

"""
서브 비디오 재생하는 함수
@Author: 류일웅
@Since: 2019-05-13
@Parameter: pressed_button, sub_play_video, key
"""
def play_sub_video(pressed_button, sub_play_video, key):
    print("press button", str(key))

    # 서브 비디오가 재생 안될 경우
    if not sub_cap[pressed_button].isOpened:
        print("Sub cap error opening video file")
        return

    while sub_cap[pressed_button].isOpened():
        sub_ret, sub_frame = sub_cap[pressed_button].read()
        key = cv2.waitKey(5) & 0xFF
        if sub_ret:
            if sub_play_video:
                cv2.imshow('sub_video', sub_frame)
                cv2.waitKey(40)

                if key == ord('q'):
                    print("press button", 'q')
                    break

        else:
            break

    return


"""
숫자 버튼 누를시 기본 비디오 일시정지 하도록 만드는 함수
@Author: 류일웅
@Since: 2019-05-13
@Parameter: button
"""
def set_play_sub_video(button):
    original_play_video = False
    sub_play_video = True
    pressed_button = button - 48

    return original_play_video, sub_play_video, pressed_button


"""
서브 비디오가 종료되면 실행하는 함수
@Author: 류일웅
@Since: 2019-05-13
@Parameter: 
"""
def set_terminate_sub_video():
    original_play_video = True
    sub_play_video = False
    sub_cap[pressed_button].release()
    cv2.destroyWindow('sub_video')

    return original_play_video, sub_play_video


# 파일 경로 및 디렉토리 설정 #
execution_path = os.getcwd()
video_path = os.path.join(execution_path, 'videos/')
output_video_path = os.path.join(execution_path, 'videos/result/')
output_video_final_path = 'twice/'
play_original_video = 'twice.mp4'
# 파일 경로 및 디렉토리 설정 끝 #

# 기본 비디오와 서브 비디오 설정
cap = cv2.VideoCapture(video_path + play_original_video)
videos = [video for video in os.listdir(output_video_path + output_video_final_path) if video.endswith(".avi")]
index = 1
sub_cap = {}

# 서브 비디오 실행을 위한 설정 반복문
for video in videos:
    sub_cap[index] = cv2.VideoCapture(output_video_path + output_video_final_path + video)
    index += 1

# 기본 비디오 재생 불가할 경우
if not cap.isOpened:
    print("Error original opening video file")
    sys.exit()

original_play_video = True
sub_play_video = False
numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9']

# 기본 비디오가 재생 중인 동안
while cap.isOpened():
    ret, frame = cap.read()
    key = cv2.waitKey(5) & 0xFF
    if ret:
        if original_play_video:
            cv2.imshow('original_video', frame)
            cv2.waitKey(30)

            # 'q'를 누른경우 비디오 종료
            if key == ord('q'):
                print("press button", 'q')
                break

            # 숫자 키를 누른 경우
            elif 48 < key < 58:

                # 재생할 수 있는 서브 비디오 수를 누른 경우
                if 48 < key <= len(sub_cap) + 48:
                    original_play_video, sub_play_video, pressed_button = set_play_sub_video(key)

                    play_sub_video(pressed_button, sub_play_video, key)

                    original_play_video, sub_play_video = set_terminate_sub_video()

    else:
        break

# Release everything if job is finished
cap.release()
cv2.destroyAllWindows()
