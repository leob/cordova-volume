var volume = {
  /**
   * Retrieves the current volume of the device
   *
   * @return Volume of the device. The volume gets normalized to the
   *         range of 0.0 to 1.0.
   */
  getVolume: function (callback, stream) {
    var args = stream ? [stream] : [];

    cordova.exec(callback, null, 'Volume', 'getVolume', args);
  },

  setVolumeChangeCallback: function (callback) {
    cordova.exec(callback, null, 'Volume', 'setVolumenChangeCallback', []);
  }

};

module.exports = volume;