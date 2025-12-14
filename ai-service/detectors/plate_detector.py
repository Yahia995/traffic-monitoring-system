from ultralytics import YOLO
from utils.config import PLATE_MODEL_PATH

class PlateDetector:
    def __init__(self):
        self.model = YOLO(PLATE_MODEL_PATH) # load YOLO model for plate detection

    def detect(self, vehicle_crop):
        results = self.model(
            vehicle_crop, # source vehicle image
            conf=0.25, # confidence threshold
            verbose=False # disable verbose output
        )

        result = results[0] # first (and only) image

        boxes = result.boxes # all detected boxes

        # no boxes detected
        if len(result.boxes) == 0:
            return None
        
        # select box with highest confidence
        best_box = max(boxes, key=lambda b: float(b.conf))

        # extract box coordinates
        x1, y1, x2, y2 = best_box.xyxy[0].cpu().tolist()

        return (int(x1), int(y1), int(x2), int(y2))