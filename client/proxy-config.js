module.exports = [
  {
    context: ["/**"],
    target: "http://localhost:8080/api",
    secure: false,
  },
];

// ng serve --proxy-config proxy-config.js