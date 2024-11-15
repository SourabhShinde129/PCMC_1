bool isHttpOrHttps(String url) {
  // Use a regular expression or URI parsing to verify the scheme
  Uri? parsedUri = Uri.tryParse(url);
  if (parsedUri != null && (parsedUri.scheme == 'http' || parsedUri.scheme == 'https')) {
    return true;
  }
  return false;
}

void main() {
  String url1 = 'https://example.com';
  String url2 = 'http://example.com';
  String url3 = 'ftp://example.com';
  String url4 = 'example.com';

  print(isHttpOrHttps(url1)); // true
  print(isHttpOrHttps(url2)); // true
  print(isHttpOrHttps(url3)); // false
  print(isHttpOrHttps(url4)); // false
}
