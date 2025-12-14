from ultralytics import YOLO
from utils.config import VEHICLE_MODEL_PATH

# vehicle class IDs based on COCO dataset
VEHICLE_CLASSES = [2, 3, 5, 7] # car, motorcycle, bus, truck

class VehicleDetector:
    def __init__(self):
        self.model = YOLO(VEHICLE_MODEL_PATH) # load YOLO model

    def detect(self, frame):
        results = self.model(
            frame , # source image
            classes=VEHICLE_CLASSES, # filter only vehicle classes
            conf=0.35, # confidence threshold
            verbose=False # disable verbose output
        ) # get detections

        result = results[0] # results for first (and only) image
        
        # extract bounding boxes
        rects = []
        for box in result.boxes:
                x1, y1, x2, y2 = box.xyxy[0].cpu().tolist() # get box coordinates
                rects.append((int(x1), int(y1), int(x2), int(y2))) # append as integer tuple

        return rects