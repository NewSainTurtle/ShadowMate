const dataURItoBlob = function (dataURI: string) {
  const byteString = atob(dataURI.split(",")[1]);
  const mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];
  const ab = new ArrayBuffer(byteString.length);
  const ia = new Uint8Array(ab);
  for (var i = 0; i < byteString.length; i++) ia[i] = byteString.charCodeAt(i);
  return new Blob([ia], { type: mimeString });
};

export const resizeImage = (file: File | Blob): Promise<Blob> => {
  const canvas = document.createElement("canvas");
  const base_size = 256000; //250KB // 파일 압축 기준 사이즈
  const comp_size = 51200; //50KB

  return new Promise((resolve, reject) => {
    const image = new Image();
    image.src = URL.createObjectURL(file);
    image.onload = () => {
      let width = image.width;
      let height = image.height;
      if (file.size <= base_size) return resolve(file);

      const ratio = Math.ceil(Math.sqrt(file.size / comp_size));
      width = image.width / ratio;
      height = image.height / ratio;
      canvas.width = width;
      canvas.height = height;
      canvas.getContext("2d")?.drawImage(image, 0, 0, width, height);
      return resolve(dataURItoBlob(canvas.toDataURL("image/png")));
    };
    image.onerror = reject;
  });
};
