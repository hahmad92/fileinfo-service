export IS_CLEAN_BUILD_REQ=true
while test $# -gt 0; do
  case "$1" in
    -h|--help)
      echo "$package - attempt to capture frames"
      echo " "
      echo "$package [options] application [arguments]"
      echo " "
      echo "options:"
      echo "-h, --help                show brief help"
      echo "-s, --service=ServiceName      specify ServiceName to be used for kubectl"
      echo "-b, --cleanBuild    if flag is true then mvn clean package deploy will be executed,default value is true"
      echo "-t, --tag=tagName   specify tag Name for image to be used nexus for deployment else current GIT hash will be used"
      exit 0
      ;;
     -v)
        shift
        if test $# -gt 0; then
          export IS_IMAGE_TAG_PROVIDED=true
          export IMAGE_TAG=$1
        fi
        shift
        ;;
      -b)
        shift
        if test $# -gt 0; then
          export IS_CLEAN_BUILD_REQ=$1
        fi
        shift
        ;;
    -s)
      shift
      if test $# -gt 0; then
        export SERVICE_NAME=$1
      else
        echo "ServiceName not specified using false"
        exit 1
      fi
      shift
      ;;
    --service*)
      export SERVICE_NAME=`echo $1 | sed -e 's/^[^=]*=//g'`
      shift
      ;;
    *)
      break
      ;;
  esac
done

if [ "$IS_CLEAN_BUILD_REQ" = true ] ; then
  echo "Executing mvn clean package"
  mvn clean
  mvn package -P prod -U -DskipTests
fi

if [ "$IS_IMAGE_TAG_PROVIDED" = true ] ; then
    echo "using provided version:"$IMAGE_TAG
else
    echo "using latest as image tag: "
    export IMAGE_TAG="latest"
fi

echo "Building Image --"
echo "Image Name:"$SERVICE_NAME
echo "Image Tag:"$IMAGE_TAG

docker build -t $SERVICE_NAME:$IMAGE_TAG .



