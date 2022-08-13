VERSION_TAGS=('22.2.0' '22.1.0' '21.3.0' '20.3.4')

mkdir options-input

for tag in "${VERSION_TAGS[@]}" ; do
  echo "Update options for tag: $tag"
  file="options-input/$tag.txt"
  docker run --rm "ghcr.io/graalvm/native-image:$tag" --expert-options-all > "$file"
  if [[ "$tag" =~ ^21.* ]] ;
  then
    echo "Post-processing for $tag"
    tmp="options-input/$tag-tmp.txt"
    ghead -n -1 "$file" > "$tmp"
    mv "$tmp" "$file"
  fi
done