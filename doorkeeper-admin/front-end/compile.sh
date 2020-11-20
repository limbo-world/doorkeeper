#!/usr/bin/env bash
echo "删除原前端文件..."
cd ../src/main/webapp
rm -rf static
rm -rf favicon.ico
rm -rf index.html
cd ../../../front-end

echo "执行前端编译..."
npm install
npm run build

echo "前端编译完成开始拷贝文件..."
cp -r dist/ ../src/main/webapp/
echo "前端打包完成!"