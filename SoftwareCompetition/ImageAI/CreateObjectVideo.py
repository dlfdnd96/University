"""
비디오에서 각 객체 인식된 영상을 저장하는 프로그램
yolo.h5 파일 필요

@Author: 류일웅
@Since: 2019-05-13
"""
from imageai.Detection import VideoObjectDetection
import os
import cv2
import glob
import re

"""
비디오 객체 인식할 때, 프레임당 인식된 객체를 보여주는 함수. YOLOv3를 이용

@Author: 류일웅
@Since: 2019-05-13
@Param: frame_number, output_array, output_count
"""
def for_frame(frame_number, output_array, output_count):
    print("For frame: ", frame_number)
    print("Output for each object: ", output_array)
    print("Output count for unique objects: ", output_count)
    print("---------------------End of a frame ---------------------")


"""
비디오에서 원하는 객체만 인식하는 함수

@Author: 류일웅
@Since: 2019-05-13
@Param: 
"""
def detect_custom_object():
    custom = detector.CustomObjects(person=True)

    detector.detectCustomObjectsFromVideo(
        custom_objects=custom,
        input_file_path=os.path.join(video_path, "cctv.mp4"),
        output_file_path=os.path.join(video_path, "cctv_working"),
        frames_per_second=30,
        log_progress=True,
        per_frame_function=for_frame,
    )


"""
비디오에서 모든 객체를 인식하는 함수

@Author: 류일웅
@Since: 2019-05-13
@Param: 
"""
def detect_object():
    detector.detectObjectsFromVideo(
       input_file_path=os.path.join(video_path, "training.mp4"),
       output_file_path=os.path.join(video_path, "training_working"),
       frames_per_second=30,
       log_progress=True,
       per_frame_function=for_frame,
    )


"""
파일 이름을 오름차순으로 정렬하는 함수

@Author: 류일웅
@Since: 2019-05-14
@Param: 
"""
def atoi(text):
    return int(text) if text.isdigit() else text


def natural_keys(text):
    return [atoi(c) for c in re.split(r'(\d+)', text)]


# 파일 경로 및 디렉토리 설정 #
execution_path = os.getcwd()
video_path = os.path.join(execution_path, 'videos/')
working_video_file_name = 'cctv_working.avi'
frames_original_path = os.path.join(execution_path, 'frames/')
frames_final_path = 'cctv/'
crop_frame_original_path = os.path.join(execution_path, 'crop-frames/')
crop_frame_final_path = 'cctv/'
output_video_path = os.path.join(execution_path, 'videos/result/')
output_video_final_path = 'cctv/'
coordinates_file_path = os.path.join(execution_path, 'coordinate/')
coordinate_file_name = 'cctv.txt'
frame_file_name = frames_original_path + frames_final_path + 'cctv_frame_%d.jpg'
crop_frame_file_name = crop_frame_original_path + crop_frame_final_path
# 파일 경로 및 디렉토리 설정 끝 #

# 비디오에서 객체 인식하는 부분 #
detector = VideoObjectDetection()
detector.setModelTypeAsYOLOv3()
detector.setModelPath(os.path.join(execution_path, "yolo.h5"))
detector.loadModel()

detect_custom_object()
# 비디오에서 객체 인식하는 부분 끝 #

# 객체인식된 비디오를 프레임단위로 저장하는 부분 #
print("Saving frame-by-frame for video of each detected object")
cap = cv2.VideoCapture(video_path + working_video_file_name)
file_number = 0

if not os.path.isdir(frames_original_path + frames_final_path):
    os.mkdir(frames_original_path + frames_final_path)

while cap.isOpened():
    ret, frame = cap.read()

    if not ret:
        print("Cap error opening video file")
        break

    name = frame_file_name % file_number
    cv2.imwrite(name, frame)

    file_number += 1

cap.release()
cv2.destroyAllWindows()
# 객체인식된 비디오를 프레임단위로 저장하는 부분 끝 #

# 인식된 각 객체만 따로 프레임 단위로 저장 #
print("Saving frame-by-frame for size of each detected object")
frames = glob.glob(frames_original_path + frames_final_path + '*.jpg')
frames.sort(key=natural_keys)

coordinate_file = open(os.path.join(coordinates_file_path, coordinate_file_name), 'r')
file_lines = coordinate_file.readlines()

# 객체 인식 프레임의 각 좌표 배열 초기화
frame_info = []
frame_class_array = []
frame_left_array = []
frame_top_array = []
frame_right_array = []
frame_bottom_array = []

# 객체 인식 프레임의 각 넓이와 높이 배열 초기화
diff_frame_width_array = []
diff_frame_height_array = []

# 객체인식 기록이 적힌 파일의 각 행마다 클래스, 좌표의 인덱스를 모아놓는 배열 초기화
index_collection_array = []

# 프레임 넓이와 높이
frame = cv2.imread(frames[0])
frame_max_height, frame_max_width, layers = frame.shape

index = 0

# 객체인식 기록이 적힌 파일의 각 행마다 클래스, 좌표, 넓이, 높이 구하는 반복문
for line in file_lines:
    # 1번 인덱스는 클래스, 6번 인덱스부터 9번인덱스는 좌표 값이다
    class_index = 1
    left_coordinate_index = 6
    top_coordinate_index = 7
    right_coordinate_index = 8
    bottom_coordinate_index = 9

    # 파일 각 행을 정규화
    process_content = re.compile('\w+')
    normalization_result = process_content.findall(line)

    # 파일의 각 행마다 클래스, 좌표값을 구하는 반복문
    # 탐지된 객체당 인덱스가 10씩 차이가 난다
    for file_index in range(len(normalization_result)):

        if file_index == class_index:
            index_collection_array.append(file_index)
            class_index += 10

        elif file_index == left_coordinate_index:
            index_collection_array.append(file_index)
            left_coordinate_index += 10

        elif file_index == top_coordinate_index:
            index_collection_array.append(file_index)
            top_coordinate_index += 10

        elif file_index == right_coordinate_index:
            index_collection_array.append(file_index)
            right_coordinate_index += 10

        elif file_index == bottom_coordinate_index:
            index_collection_array.append(file_index)
            bottom_coordinate_index += 10

    length_normalization_array = int((len(normalization_result)) / 10)

    # 각 객체에 대한 클래스와 좌표값을 미리 선언된 배열에 해당하는 배열에 추가하는 반복문
    for i in range(length_normalization_array):

        # 클래스부터 좌표값까지 인덱스 크기가 5이다
        for aa in range(5):

            # 클래스는 클래스 배열에 추가
            if aa == 0:
                frame_info.append(normalization_result[index_collection_array[index]])

            else:

                # 왼쪽과 위쪽의 좌표값을 프레임 정보배열에 저장
                if aa == 1 or aa == 2:

                    # 각 좌표값이 0보다 크면 그 값을 저장
                    if (int(normalization_result[index_collection_array[index + aa]]) - 50) > 0:
                        frame_info.append(int(normalization_result[index_collection_array[index + aa]]) - 50)

                    # 0보다 작으면 0으로 저장
                    else:
                        frame_info.append(0)

                # 오른쪽과 위쪽 좌표는 더한 값으로 저장
                # 아마 이 부분 수정해야 할듯. 최대 사진 크기 값으로 조건문 추가하는것을.
                elif aa == 3 or aa == 4:
                    frame_info.append(int(normalization_result[index_collection_array[index + aa]]) + 50)

        # 프레임 정보 배열에 저장되어있던 좌표 값들을 각각 좌표 배열에 저장
        frame_left_array.append([frame_info[index], frame_info[index + 1]])
        frame_top_array.append([frame_info[index], frame_info[index + 2]])
        frame_right_array.append([frame_info[index], frame_info[index + 3]])
        frame_bottom_array.append([frame_info[index], frame_info[index + 4]])

        # 넓이와 높이 계산
        frame_width = frame_info[index + 3] - frame_info[index + 1]
        frame_height = frame_info[index + 4] - frame_info[index + 2]

        # 계산된 넓이와 높이를 각각 해당하는 배열에 저장
        diff_frame_width_array.append([frame_info[index], frame_width])
        diff_frame_height_array.append([frame_info[index], frame_height])

        index += 5

# 첫번째 클래스만 클래스 배열에 저장
index = 0
frame_class_array.append(frame_info[index])
first_class = frame_info[index]

index = 5

# 나머지 클래스들을 발견하면 저장
while frame_info:

    if first_class == frame_info[index]:
        break

    else:
        frame_class_array.append(frame_info[index])
        index += 5

missed_class_array = []
# 누락된 객체인식 부분 찾는 반복문
for count, line in enumerate(file_lines):
    process_content = re.compile('\w+')
    normalization_result = process_content.findall(line)

    # 누락된 객체인식이 있다면
    if len(normalization_result) != len(frame_class_array) * 10:
        missed_class_number = (len(normalization_result) / 10) # 누락된 객체인식 수
        class_dictionary = {}
        class_index = 1

        # 누락되지 않은 클래스들을 딕셔너리형태로 저장하는 반복문
        for index in range(int(missed_class_number)):
            class_dictionary[index] = normalization_result[class_index]
            class_index += 10

        # 누락된 객체인식들이 존재하는 프레임을 찾는 반복문
        for frame_class in frame_class_array:

            # 누락된 것이 있다면 누락된 클래스 배열에 저장
            if frame_class not in class_dictionary.values():
                missed_class_array.append([count, frame_class])

max_width = 0
class_max_width = []
sort_diff_frame_width_array = sorted(diff_frame_width_array, key=lambda dw: dw[0])

# 각 클래스의 최대 넓이 값을 구하는 반복문
for count, width in enumerate(sort_diff_frame_width_array):

    if max_width < sort_diff_frame_width_array[count][1]:
        max_width = sort_diff_frame_width_array[count][1]

    if count == len(sort_diff_frame_width_array) - 1:
        class_max_width.append([width[0], max_width])
        break

    # 클래스가 변경된 경우
    if width[0] != sort_diff_frame_width_array[count + 1][0]:
        class_max_width.append([width[0], max_width])
        max_width = 0

sort_frame_left_array = sorted(frame_left_array, key=lambda ila: ila[0])
over_width_class_array = []
previous_detect_class = ''
index = 0

# 각 클래스의 오른쪽 좌표 값이 사진 크기보다 넘어가는지 확인하는 반복문
for count, left_coordinate in enumerate(sort_frame_left_array):

    if left_coordinate[0] == previous_detect_class:
        continue

    if left_coordinate[0] != class_max_width[index][0]:
        index += 1

    right = left_coordinate[1] + class_max_width[index][1]

    if right > frame_max_width:  # 이미지의 넓이 크기로 바꿔야함
        over_width_class_array.append([left_coordinate[0]])
        previous_detect_class = left_coordinate[0]
        index += 1

sort_frame_right_array = sorted(frame_right_array, key=lambda ira: ira[0])
fixed_left_coordinate_array = []
fixed_right_coordinate_array = []
min_left_coordinate = 999
max_right_coordinate = 0

# 오른쪽 좌표 값이 사진 크기 값보다 큰 경우
if len(over_width_class_array):

    # 왼쪽 좌표의 최소값, 오른쪽 좌표의 최대값을 구하는 반복문
    for class_name in over_width_class_array:

        # 왼쪽 좌표 최소값을 구하는 반복문
        for count, left_coordinate in enumerate(sort_frame_left_array):

            if left_coordinate[0] != class_name[0]:

                if min_left_coordinate == 999:
                    continue

                else:
                    fixed_left_coordinate_array.append([class_name[0], min_left_coordinate])
                    min_left_coordinate = 999
                    break

            if count == len(sort_frame_left_array) - 1:
                fixed_left_coordinate_array.append([class_name[0], min_left_coordinate])
                min_left_coordinate = 999
                break

            if min_left_coordinate > left_coordinate[1]:
                min_left_coordinate = left_coordinate[1]

        # 오른쪽 좌표 최대값을 구하는 반복문
        for count, right_coordinate in enumerate(sort_frame_right_array):

            if right_coordinate[0] != class_name[0]:

                if max_right_coordinate == 0:
                    continue

                else:
                    fixed_right_coordinate_array.append([class_name[0], max_right_coordinate])
                    max_right_coordinate = 0
                    break

            if count == len(sort_frame_right_array) - 1:
                fixed_right_coordinate_array.append([class_name[0], max_right_coordinate])
                max_right_coordinate = 0
                break

            if max_right_coordinate < right_coordinate[1]:
                max_right_coordinate = right_coordinate[1]

max_height = 0
class_max_height = []
sort_diff_frame_height_array = sorted(diff_frame_height_array, key=lambda dh: dh[0])

# 각 클래스의 최대 높이 값을 구하는 반복문
for count, height in enumerate(sort_diff_frame_height_array):

    if max_height < sort_diff_frame_height_array[count][1]:
        max_height = sort_diff_frame_height_array[count][1]

    if count == len(sort_diff_frame_height_array) - 1:
        class_max_height.append([height[0], max_height])
        break

    # 클래스가 변경된 경우
    if height[0] != sort_diff_frame_height_array[count + 1][0]:
        class_max_height.append([height[0], max_height])
        max_height = 0

sort_frame_top_array = sorted(frame_top_array, key=lambda ita: ita[0])
over_height_class_array = []
previous_detect_class = ''
index = 0

# 각 클래스의 아래쪽 좌표 값이 사진 크기보다 넘어가는 확인하는 반복문
for top_coordinate in sort_frame_top_array:

    if top_coordinate[0] == previous_detect_class:
        continue

    if top_coordinate[0] != class_max_height[index][0]:
        index += 1

    bottom = top_coordinate[1] + class_max_height[index][1]

    if bottom > frame_max_height: # 이미지 높이 크기로 바꿔야함
        over_height_class_array.append([top_coordinate[0]])
        previous_detect_class = top_coordinate[0]
        index += 1

sort_frame_bottom_array = sorted(frame_bottom_array, key=lambda iba: iba[0])
fixed_top_coordinate_array = []
fixed_bottom_coordinate_array = []
min_top_coordinate = 999
max_bottom_coordinate = 0

# 아래쪽 좌표 값이 사진 크기 값보다 큰 경우
if len(over_height_class_array):

    # 위쪽 좌표의 최소값, 아래쪽 좌표의 최대값을 구하는 반복문
    for class_name in over_height_class_array:

        # 위쪽 좌표 최소값을 구하는 반복문
        for count, top_coordinate in enumerate(sort_frame_top_array):

            if top_coordinate[0] != class_name[0]:

                if min_top_coordinate == 999:
                    continue

                else:
                    fixed_top_coordinate_array.append([class_name[0], min_top_coordinate])
                    min_top_coordinate = 999
                    break

            if count == len(sort_frame_top_array) - 1:
                fixed_top_coordinate_array.append([class_name[0], min_top_coordinate])
                min_top_coordinate = 999
                break

            if min_top_coordinate > top_coordinate[1]:
                min_top_coordinate = top_coordinate[1]

        # 아래쪽 좌표 최대값을 구하는 반복문
        for count, bottom_coordinate in enumerate(sort_frame_bottom_array):

            if bottom_coordinate[0] != class_name[0]:

                if max_bottom_coordinate == 0:
                    continue

                else:
                    fixed_bottom_coordinate_array.append([class_name[0], max_bottom_coordinate])
                    max_bottom_coordinate = 0
                    break

            if count == len(sort_frame_bottom_array) - 1:
                fixed_bottom_coordinate_array.append([class_name[0], max_bottom_coordinate])
                max_bottom_coordinate = 0
                break

            if max_bottom_coordinate < bottom_coordinate[1]:
                max_bottom_coordinate = bottom_coordinate[1]

if not os.path.isdir(crop_frame_original_path + crop_frame_final_path):
    os.mkdir(crop_frame_original_path + crop_frame_final_path)

index = 0

# 각 객체 인식된 부분만 프레임으로 만드는 반복문
for count in range(len(file_lines)):
    img = cv2.imread(frames[count])
    roi = None

    # 클래스 단위로 프레임 생성하는 반복문
    for inner_count, frame_class in enumerate(frame_class_array):
        cannot_process = False

        # 누락된 클래스가 있는 경우
        for missed_class in missed_class_array:

            if count == missed_class[0] and frame_class == missed_class[1]:
                cannot_process = True
                break

        if not cannot_process:
            top = frame_top_array[index][1]
            bottom = frame_top_array[index][1] + class_max_height[inner_count][1]
            left = frame_left_array[index][1]
            right = frame_left_array[index][1] + class_max_width[inner_count][1]

            fixed_top = 0
            fixed_bottom = 0
            fixed_left = 0
            fixed_right = 0

            right_flag = 0
            bottom_flag = 0

            # 객체인식된 프레임 넓이가 전체 프레임 넓이보다 크다면
            if over_width_class_array:

                for width_class in over_width_class_array:

                    if width_class[0] == frame_class:
                        right_flag = 1

                        # 왼쪽 좌표 최소값 설정
                        for left_coordinate in fixed_left_coordinate_array:

                            if left_coordinate[0] == frame_class:
                                fixed_left = left_coordinate[1]

                        for right_coordinate in fixed_right_coordinate_array:

                            if right_coordinate[0] == frame_class:
                                fixed_right = right_coordinate[1]

            # 객체인식된 프레임 높이가 전체 프레임 높이보다 크다면
            if over_height_class_array:

                for height_class in over_height_class_array:

                    if height_class[0] == frame_class:
                        bottom_flag = 1

                        # 위쪽 좌표 최소값 설정
                        for top_coordinate in fixed_top_coordinate_array:

                            if top_coordinate[0] == frame_class:
                                fixed_top = top_coordinate[1]

                        # 아래쪽 좌표 최대값 설정
                        for bottom_coordinate in fixed_bottom_coordinate_array:

                            if bottom_coordinate[0] == frame_class:
                                fixed_bottom = bottom_coordinate[1]

            # 프레임으로 만들 영역 저장
            if not right_flag and not bottom_flag:
                roi = img[top:bottom, left:right]

            elif right_flag and bottom_flag:
                roi = img[fixed_top:fixed_bottom, fixed_left:fixed_right]

            elif bottom_flag:
                roi = img[fixed_top:fixed_bottom, left:right]

            elif right_flag:
                roi = img[top:bottom, fixed_left:fixed_right]

            name = crop_frame_file_name + frame_class + '_crop_frame_%d.jpg' % count
            # 프레임 생성
            cv2.imwrite(name, roi)

            index += 1

coordinate_file.close()
# 인식된 각 객체만 따로 프레임 단위로 저장 끝#

# 각 객체 프레임을 비디오로 생성 #
print("Generate video for each frame of detected object")
# 이미지 확장자인 파일만 저장
frames = [img for img in os.listdir(crop_frame_original_path + crop_frame_final_path) if img.endswith(".jpg")]
frames.sort(key=natural_keys)

create_video_array = []
index = 0

os.chdir(crop_frame_original_path + crop_frame_final_path)

if not os.path.isdir(output_video_path + output_video_final_path):
    os.mkdir(output_video_path + output_video_final_path)

# 객체마다 비디오 만드는 반복문
for line, file_name in enumerate(frames):

    # 클래스가 변경 됐으면
    if file_name.split('_')[0] != frame_class_array[index]:
        video_name = 'result_crop_video_' + str(frames[line - 1].split('_')[0]) + '.avi'
        video = cv2.VideoWriter(output_video_path + output_video_final_path + video_name, 0, 30, (width, height))

        # 비디오 생성 반복문
        for i in range(len(create_video_array)):
            video.write(create_video_array[i])

        cv2.destroyAllWindows()
        video.release()
        index += 1

    frame = cv2.imread(file_name)
    height, width, layers = frame.shape
    create_video_array.append(frame)

    # 반복문의 마지막이면
    if line == len(frames) - 1:
        video_name = 'result_crop_video_' + str(file_name.split('_')[0]) + '.avi'
        video = cv2.VideoWriter(output_video_path + output_video_final_path + video_name, 0, 30, (width, height))

        for i in range(len(create_video_array)):
            video.write(create_video_array[i])

print("All jobs are finish")
# 각 객체 프레임을 비디오로 생성 끝 #
